package eu.zkkn.android.disruptions.data

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import eu.zkkn.android.disruptions.utils.ioThread


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


    fun getSubscriptions(): LiveData<List<Subscription>> = dao.getAll()

    @WorkerThread fun getAllLineNames() = dao.getAllLineNames()

    //TODO: prevent inserting two same line names
    fun addSubscription(lineName: String) = ioThread {
        dao.insert(Subscription(0, lineName.uppercase()))
    }

    fun removeSubscription(lineName: String) = ioThread {
        dao.deleteByLineName(lineName.uppercase())
    }

}
