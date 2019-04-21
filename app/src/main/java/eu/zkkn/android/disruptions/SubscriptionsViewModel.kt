package eu.zkkn.android.disruptions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.firebase.messaging.FirebaseMessaging
import eu.zkkn.android.disruptions.data.Subscription
import eu.zkkn.android.disruptions.data.SubscriptionRepository
import eu.zkkn.disruptions.common.FcmConstants


class SubscriptionsViewModel(application: Application) : AndroidViewModel(application) {

    private val subscriptionRepository by lazy { SubscriptionRepository.getInstance(application) }

    val subscriptions: LiveData<List<Subscription>> by lazy {
        subscriptionRepository.getSubscriptions()
    }

    fun addSubscription(lineName: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(FcmConstants.topicNameForLine(lineName))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    subscriptionRepository.addSubscription(lineName.trim())
                }
            }
    }

    fun removeSubscription(lineName: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(FcmConstants.topicNameForLine(lineName))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    subscriptionRepository.removeSubscription(lineName.trim())
                }
            }
    }

}
