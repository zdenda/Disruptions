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


    fun getTopics(context: Context): Set<String> {
        val default = setOf<String>()
        return getPreferences(context).getStringSet(PREF_KEY_TOPICS, default) ?: default
    }

    fun addTopic(context: Context, topic: String) {
        getPreferences(context).edit().putStringSet(PREF_KEY_TOPICS,
            getTopics(context).toMutableSet().apply { add(topic) })
            .apply()
    }

    fun removeTopic(context: Context, topic: String) {
        getPreferences(context).edit().putStringSet(PREF_KEY_TOPICS,
            getTopics(context).toMutableSet().apply { remove(topic) })
            .apply()
    }

}
