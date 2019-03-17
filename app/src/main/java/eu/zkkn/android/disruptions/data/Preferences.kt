package eu.zkkn.android.disruptions.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


object Preferences {

    private const val PREF_KEY_TOPICS = "topics"

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

}
