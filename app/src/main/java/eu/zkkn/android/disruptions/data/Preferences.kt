package eu.zkkn.android.disruptions.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import eu.zkkn.android.disruptions.workers.RefreshSubscriptionsWorker


object Preferences {

    @Suppress("unused") // keep old keys, so they wouldn't be reused for other purposes
    private const val PREF_KEY_TOPICS = "topics"

    private const val PREF_KEY_PERIODIC_SUBSCRIPTION_REFRESH =
        "periodicSubscriptionRefresh-v${RefreshSubscriptionsWorker.VERSION}"
    private const val PREF_KEY_LAST_SUBSCRIPTION_REFRESH =
        "lastSubscriptionRefreshTime-v${RefreshSubscriptionsWorker.VERSION}"
    private const val PREF_KEY_LAST_HEARTBEAT_RECEIVED = "lastHeartbeatReceivedTime"
    private const val PREF_KEY_LAST_HEARTBEAT_SENT = "lastHeartbeatSentTime"
    private const val PREF_KEY_FIRST_RUN = "firstRun-v1"
    private const val PREF_KEY_ENABLE_REALTIME_POSITIONS = "enableRealtimePositionsMap"

    private lateinit var preferences: SharedPreferences


    private fun getPreferences(context: Context): SharedPreferences {
        if (!Preferences::preferences.isInitialized) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        }
        return preferences
    }


    fun setPeriodicSubscriptionRefresh(context: Context) {
        getPreferences(context).edit {
            putLong(PREF_KEY_PERIODIC_SUBSCRIPTION_REFRESH, System.currentTimeMillis())
        }
    }

    fun isPeriodicSubscriptionRefreshEnabled(context: Context): Boolean {
        return 0L != getPreferences(context).getLong(PREF_KEY_PERIODIC_SUBSCRIPTION_REFRESH, 0L)
    }

    fun setLastSubscriptionRefreshTime(context: Context, timeMs: Long = System.currentTimeMillis()) {
        getPreferences(context).edit {
            putLong(PREF_KEY_LAST_SUBSCRIPTION_REFRESH, timeMs).apply()
        }
    }

    fun getLastSubscriptionRefreshTime(context: Context): Long {
        return getPreferences(context).getLong(PREF_KEY_LAST_SUBSCRIPTION_REFRESH, 0L)
    }

    fun setLastHeartbeatReceivedTime(context: Context, timeMs: Long) {
        getPreferences(context).edit {
            putLong(PREF_KEY_LAST_HEARTBEAT_RECEIVED, timeMs).apply()
        }
    }

    fun getLastHeartbeatReceivedTime(context: Context): Long {
        return getPreferences(context).getLong(PREF_KEY_LAST_HEARTBEAT_RECEIVED, 0L)
    }

    fun setLastHeartbeatSentTime(context: Context, timeMs: Long) {
        getPreferences(context).edit {
            putLong(PREF_KEY_LAST_HEARTBEAT_SENT, timeMs).apply()
        }
    }

    fun getLastHeartbeatSentTime(context: Context): Long {
        return getPreferences(context).getLong(PREF_KEY_LAST_HEARTBEAT_SENT, 0L)
    }

    fun isFirstRun(context: Context): Boolean {
        val prefs = getPreferences(context)
        val value = prefs.getBoolean(PREF_KEY_FIRST_RUN, true)
        // set to false after first time
        if (value) prefs.edit { putBoolean(PREF_KEY_FIRST_RUN, false).apply() }
        return value
    }

    fun setRealtimePositionsEnabled(context: Context, enabled: Boolean) {
        getPreferences(context).edit {
            putBoolean(PREF_KEY_ENABLE_REALTIME_POSITIONS, enabled).apply()
        }
    }

    fun resetRealtimePositionsEnabled(context: Context) {
        getPreferences(context).edit {
            remove(PREF_KEY_ENABLE_REALTIME_POSITIONS).apply()
        }
    }

    fun isRealtimePositionsEnabled(context: Context): Boolean? {
        val pref = getPreferences(context)
        return if (pref.contains(PREF_KEY_ENABLE_REALTIME_POSITIONS)) {
            pref.getBoolean(PREF_KEY_ENABLE_REALTIME_POSITIONS, true)
        } else {
            null
        }
    }

}
