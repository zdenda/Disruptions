package eu.zkkn.android.disruptions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.firebase.messaging.FirebaseMessaging
import eu.zkkn.android.disruptions.data.Subscription
import eu.zkkn.android.disruptions.data.SubscriptionRepository


class SubscriptionsViewModel(application: Application) : AndroidViewModel(application) {

    private val subscriptionRepository by lazy { SubscriptionRepository.getInstance(application) }

    val subscriptions: LiveData<List<Subscription>> by lazy {
        subscriptionRepository.getSubscriptions()
    }

    fun addSubscription(lineName: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic(lineName))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    subscriptionRepository.addSubscription(lineName.trim())
                }
            }
    }

    fun removeSubscription(lineName: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic(lineName))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    subscriptionRepository.removeSubscription(lineName.trim())
                }
            }
    }


    private fun topic(line: String): String {
        // remove spaces a use lower case for the name of line
        return "topic_pid_${line.trim().toLowerCase()}"
    }

}
