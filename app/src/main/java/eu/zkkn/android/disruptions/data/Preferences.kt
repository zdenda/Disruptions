package eu.zkkn.android.disruptions.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import eu.zkkn.android.disruptions.workers.RefreshSubscriptionsWorker


object Preferences {

    private const val PREF_KEY_TOPICS = "topics"
    private const val PREF_KEY_PERIODIC_SUBSCRIPTION_REFRESH =
        "periodicSubscriptionRefresh-v${RefreshSubscriptionsWorker.VERSION}"
    private const val PREF_KEY_LAST_SUBSCRIPTION_REFRESH =
        "lastSubscriptionRefreshTime-v${RefreshSubscriptionsWorker.VERSION}"
    private const val PREF_KEY_LAST_HEARTBEAT_RECEIVED = "lastHeartbeatReceivedTime"
    private const val PREF_KEY_LAST_HEARTBEAT_SENT = "lastHeartbeatSentTime"

    private lateinit var preferences: SharedPreferences


    private fun getPreferences(context: Context): SharedPreferences {
        if (!Preferences::preferences.isInitialized) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        }
        return preferences
    }

    @Deprecated("Only for migration to Database storage")
    fun getTopics(context: Context): Set<String> {
        val default = setOf<String>()
        return getPreferences(context).getStringSet(PREF_KEY_TOPICS, default) ?: default
    }

    fun setPeriodicSubscriptionRefresh(context: Context) {
        getPreferences(context).edit()
            .putLong(PREF_KEY_PERIODIC_SUBSCRIPTION_REFRESH, System.currentTimeMillis())
            .apply()
    }

    fun isPeriodicSubscriptionRefreshEnabled(context: Context): Boolean {
        return 0L != getPreferences(context).getLong(PREF_KEY_PERIODIC_SUBSCRIPTION_REFRESH, 0L)
    }

    fun setLastSubscriptionRefreshTime(context: Context, timeMs: Long = System.currentTimeMillis()) {
        getPreferences(context).edit().putLong(PREF_KEY_LAST_SUBSCRIPTION_REFRESH, timeMs).apply()
    }

    fun getLastSubscriptionRefreshTime(context: Context): Long {
        return getPreferences(context).getLong(PREF_KEY_LAST_SUBSCRIPTION_REFRESH, 0L)
    }

    fun setLastHeartbeatReceivedTime(context: Context, timeMs: Long) {
        getPreferences(context).edit().putLong(PREF_KEY_LAST_HEARTBEAT_RECEIVED, timeMs).apply()
    }

    fun getLastHeartbeatReceivedTime(context: Context): Long {
        return getPreferences(context).getLong(PREF_KEY_LAST_HEARTBEAT_RECEIVED, 0L)
    }

    fun setLastHeartbeatSentTime(context: Context, timeMs: Long) {
        getPreferences(context).edit().putLong(PREF_KEY_LAST_HEARTBEAT_SENT, timeMs).apply()
    }

    fun getLastHeartbeatSentTime(context: Context): Long {
        return getPreferences(context).getLong(PREF_KEY_LAST_HEARTBEAT_SENT, 0L)
    }

}
