package eu.zkkn.android.disruptions.utils

import android.app.Activity
import android.content.Context
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics


object Analytics {

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    fun init(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context.applicationContext)
    }

    fun sendScreenView(screenName: String, activity: Activity) {
        firebaseAnalytics.setCurrentScreen(activity, screenName, null /* class override */)
    }

    fun logSubscribe(topic: String) {
        // "topic" as a event name doesn't work, so use "topic_name"
        firebaseAnalytics.logEvent("subscribe", bundleOf("topic_name" to topic))
    }

    fun logUnsubscribe(topic: String) {
        firebaseAnalytics.logEvent("unsubscribe", bundleOf("topic_name" to topic))
    }

}
