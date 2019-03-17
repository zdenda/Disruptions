package eu.zkkn.android.disruptions.data

import android.content.Context


class SubscriptionRepository private constructor(private val dao: SubscriptionDao) {

    companion object {
        // For Singleton instantiation
        @Volatile
        private var INSTANCE: SubscriptionRepository? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SubscriptionRepository(AppDatabase.getInstance(context).subscriptionDao())
                    .also { INSTANCE = it }
            }
    }


    fun getSubscriptions(): Set<String> = dao.getAll().map { it.lineName }.toSet()

    fun addSubscription(lineName: String) = dao.insert(Subscription(0, lineName.toUpperCase()))

    fun removeSubscription(lineName: String) = dao.deleteByLineName(lineName.toUpperCase())

}
