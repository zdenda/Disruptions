package eu.zkkn.android.disruptions.utils

import android.content.Context
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics


object Analytics {

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    //TODO: initialize in Application.onCreate(), so not only events in activities could be tracked,
    // but also those in services and notifications
    fun init(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context.applicationContext)
    }


    fun logSubscribe(topic: String) {
        // "topic" as a event name doesn't work, so use "topic_name"
        firebaseAnalytics.logEvent("subscribe", bundleOf("topic_name" to topic))
    }

    fun logUnsubscribe(topic: String) {
        firebaseAnalytics.logEvent("unsubscribe", bundleOf("topic_name" to topic))
    }

}
