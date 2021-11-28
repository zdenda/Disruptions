package eu.zkkn.android.disruptions.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import eu.zkkn.android.disruptions.R


class AppNotificationManager(context: Context) {

    companion object {
        const val DISRUPTIONS_CHANNEL_ID = "disruptions"
    }

    private val appContext: Context = context.applicationContext

    private val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(appContext)
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //TODO add channel description
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    DISRUPTIONS_CHANNEL_ID,
                    appContext.getString(R.string.notification_channel_disruptions_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }

    fun notify(id: Int, notification: Notification) {
        createNotificationChannel()
        notificationManager.notify(id, notification)
    }

}
