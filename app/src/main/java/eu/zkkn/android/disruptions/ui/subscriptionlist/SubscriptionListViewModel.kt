package eu.zkkn.android.disruptions.ui.subscriptionlist

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.FirebaseMessaging
import eu.zkkn.android.disruptions.R
import eu.zkkn.android.disruptions.data.Subscription
import eu.zkkn.android.disruptions.data.SubscriptionRepository
import eu.zkkn.android.disruptions.utils.Analytics
import eu.zkkn.android.disruptions.utils.isValidLineName
import eu.zkkn.disruptions.common.FcmConstants


class SubscriptionListViewModel(application: Application) : AndroidViewModel(application) {

    class SubscribeState(val inProgress: Boolean, @StringRes private val errorResId: Int? = null) {
        private var handledError = false

        val errorMsgResIdIfNotHandled: Int? get() {
            if (handledError) return null
            handledError = true
            return errorResId
        }
    }

    private val subscriptionRepository by lazy { SubscriptionRepository.getInstance(application) }

    private val _subscribeStatus: MutableLiveData<SubscribeState> =
        MutableLiveData(SubscribeState(false, null))

    // publicly expose just LiveData (hide MutableLiveData)
    val subscribeStatus: LiveData<SubscribeState> get() = _subscribeStatus

    val subscriptions: LiveData<List<Subscription>> by lazy {
        subscriptionRepository.getSubscriptions()
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
