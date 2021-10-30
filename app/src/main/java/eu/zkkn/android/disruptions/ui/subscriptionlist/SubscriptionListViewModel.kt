package eu.zkkn.android.disruptions.ui.subscriptionlist

import android.app.Application
import android.util.Log
import androidx.annotation.StringRes
import androidx.core.content.PackageManagerCompat
import androidx.core.content.PackageManagerCompat.UnusedAppRestrictionsStatus
import androidx.core.content.UnusedAppRestrictionsConstants.API_30
import androidx.core.content.UnusedAppRestrictionsConstants.API_30_BACKPORT
import androidx.core.content.UnusedAppRestrictionsConstants.API_31
import androidx.core.content.UnusedAppRestrictionsConstants.DISABLED
import androidx.core.content.UnusedAppRestrictionsConstants.ERROR
import androidx.core.content.UnusedAppRestrictionsConstants.FEATURE_NOT_AVAILABLE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.FirebaseMessaging
import eu.zkkn.android.disruptions.BuildConfig
import eu.zkkn.android.disruptions.R
import eu.zkkn.android.disruptions.data.Subscription
import eu.zkkn.android.disruptions.data.SubscriptionRepository
import eu.zkkn.android.disruptions.utils.Analytics
import eu.zkkn.android.disruptions.utils.getBackgroundExecutor
import eu.zkkn.android.disruptions.utils.isValidLineName
import eu.zkkn.disruptions.common.FcmConstants


class SubscriptionListViewModel(application: Application) : AndroidViewModel(application) {

    enum class AppHibernationState {
        UNKNOWN, ERROR, DISABLED, ENABLED
    }

    class SubscribeState(val inProgress: Boolean, @StringRes private val errorResId: Int? = null) {
        private var handledError = false

        val errorMsgResIdIfNotHandled: Int? get() {
            if (handledError) return null
            handledError = true
            return errorResId
        }
    }

    private val subscriptionRepository by lazy { SubscriptionRepository.getInstance(application) }

    private val _appHibernationStatus: MutableLiveData<AppHibernationState> by lazy {
        MutableLiveData(AppHibernationState.UNKNOWN).also { mutableLiveData ->
            loadAppHibernationStatusToLiveData(mutableLiveData)
        }
    }

    private fun loadAppHibernationStatusToLiveData(
        mutableLiveData: MutableLiveData<AppHibernationState>
    ) {
        PackageManagerCompat.getUnusedAppRestrictionsStatus(getApplication()).apply {
            addListener({
                mutableLiveData.postValue(appHibernationFromUnusedAppRestrictionsStatus(get()))
            }, getBackgroundExecutor())
        }
    }

    private fun appHibernationFromUnusedAppRestrictionsStatus(
        @UnusedAppRestrictionsStatus unusedAppRestrictions: Int
    ): AppHibernationState {
        Log.d("ZKLog", "UnusedAppRestrictionsStatus: $unusedAppRestrictions")
        val status: AppHibernationState = when (unusedAppRestrictions) {
            ERROR -> AppHibernationState.ERROR
            DISABLED -> AppHibernationState.DISABLED
            FEATURE_NOT_AVAILABLE -> AppHibernationState.DISABLED
            API_30 -> AppHibernationState.DISABLED
            API_30_BACKPORT -> AppHibernationState.DISABLED
            // App Hibernation was introduced as restriction for unused apps in API 31 (Android 12)
            API_31 -> AppHibernationState.ENABLED
            // Unknown constant value (maybe a new API version)
            else -> {
                if (BuildConfig.DEBUG) {
                    throw AssertionError(
                        "Unknown value for UnusedAppRestrictionsConstants: $unusedAppRestrictions"
                    )
                }
                AppHibernationState.ERROR
            }
        }
        return status
    }

    private val _subscribeStatus: MutableLiveData<SubscribeState> =
        MutableLiveData(SubscribeState(false, null))

    // publicly expose just LiveData (hide MutableLiveData)
    val appHibernationStatus: LiveData<AppHibernationState> get() = _appHibernationStatus
    val subscribeStatus: LiveData<SubscribeState> get() = _subscribeStatus

    val subscriptions: LiveData<List<Subscription>> by lazy {
        subscriptionRepository.getSubscriptions()
    }

    fun refreshAppRestrictionsStatus() {
        loadAppHibernationStatusToLiveData(_appHibernationStatus)
    }

    fun addSubscription(lineName: String) {
        _subscribeStatus.value = SubscribeState(true)
        if (!lineName.isValidLineName()) {
            _subscribeStatus.value = SubscribeState(false, R.string.input_line_wrong_name)
            return
        }
        val topicName = FcmConstants.topicNameForLine(lineName)
        FirebaseMessaging.getInstance().subscribeToTopic(topicName)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    subscriptionRepository.addSubscription(lineName.trim())
                    _subscribeStatus.value = SubscribeState(false)
                } else {
                    _subscribeStatus.value = SubscribeState(false, R.string.fcm_subscribe_failure)
                }

            }
        Analytics.logSubscribe(topicName)
    }

    fun removeSubscription(lineName: String) {
        val topicName = FcmConstants.topicNameForLine(lineName)
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topicName)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    subscriptionRepository.removeSubscription(lineName.trim())
                }
            }
        Analytics.logUnsubscribe(topicName)
    }

}
