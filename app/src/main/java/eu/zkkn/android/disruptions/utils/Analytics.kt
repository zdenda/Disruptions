package eu.zkkn.android.disruptions.utils

import android.app.Activity
import android.content.Context
import android.provider.Settings
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics


object Analytics {

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    fun init(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context.applicationContext)
        if (!shouldExcludeDeviceFromAnalytics(context)) {
            firebaseAnalytics.setAnalyticsCollectionEnabled(true)
        }
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

    fun logSubscribeForm(lineName: String) {
        firebaseAnalytics.logEvent("form_subscribe", bundleOf("line_name" to lineName))
    }

    fun logShare(itemId: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE,
            bundleOf(FirebaseAnalytics.Param.ITEM_ID to itemId))
    }

    private fun shouldExcludeDeviceFromAnalytics(context: Context): Boolean {
        // exclude Firebase Test Lab and Google Play Pre-launch Report devices from analytics
        return "true" == Settings.System.getString(context.contentResolver, "firebase.test.lab")
    }

}
