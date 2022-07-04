package eu.zkkn.android.disruptions.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
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
        notificationManager.createNotificationChannel(
            NotificationChannel(
                DISRUPTIONS_CHANNEL_ID,
                appContext.getString(R.string.notification_channel_disruptions_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
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
