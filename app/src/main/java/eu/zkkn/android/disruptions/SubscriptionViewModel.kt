package eu.zkkn.android.disruptions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import eu.zkkn.android.disruptions.data.SubscriptionRepository


class SubscriptionViewModel(application: Application) : AndroidViewModel(application) {

    val subscriptions: LiveData<Set<String>> by lazy {
        Transformations.map(SubscriptionRepository.getInstance(application).getSubscriptions()) { subscriptions ->
            subscriptions.map { it.lineName }.toSet()
        }
    }

}
