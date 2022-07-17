package eu.zkkn.android.disruptions.utils

import android.app.Notification
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import eu.zkkn.android.disruptions.R


//TODO: This might be a singleton and lazy init of notificationManager is useless
class AppNotificationManager(context: Context) {

    companion object {
        private val TAG = AppNotificationManager::class.simpleName
        const val DISRUPTIONS_CHANNEL_ID = "disruptions"
    }

    private val appContext: Context = context.applicationContext

    private val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(appContext)
    }


    fun areNotificationsEnabled(): Boolean = notificationManager.areNotificationsEnabled()

    fun createNotificationChannel() {
        val notificationChannel = NotificationChannelCompat.Builder(
            DISRUPTIONS_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_DEFAULT
        ).setName(appContext.getString(R.string.notification_channel_disruptions_name))
            .build()
        notificationManager.createNotificationChannel(
            notificationChannel
        )
    }

    fun notify(id: Int, notification: Notification) {
        if (notificationManager.areNotificationsEnabled()) {
            createNotificationChannel()
            notificationManager.notify(id, notification)
        } else {
            Log.w(TAG, "Notification are blocked.")
        }
    }

}
