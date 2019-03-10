package eu.zkkn.android.disruptions.data

import android.annotation.SuppressLint
import android.content.Context


class SubscriptionRepository private constructor(private val appContext: Context){

    companion object {
        // For Singleton instantiation
        @SuppressLint("StaticFieldLeak") // It's an application context
        @Volatile private var instance: SubscriptionRepository? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: SubscriptionRepository(context.applicationContext).also { instance = it }
            }
    }


    fun getSubscriptions() = Preferences.getTopics(appContext)

    fun addSubscription(lineName: String) = Preferences.addTopic(appContext, lineName.toUpperCase())

    fun removeSubscription(lineName: String) = Preferences.removeTopic(appContext, lineName.toUpperCase())

}
