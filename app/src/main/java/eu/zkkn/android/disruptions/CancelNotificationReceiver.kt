package eu.zkkn.android.disruptions

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat.Action
import androidx.core.app.NotificationManagerCompat


class CancelNotificationReceiver : BroadcastReceiver() {

    companion object {

        private const val ACTION_CANCEL_NOTIFICATION = BuildConfig.APPLICATION_ID + ".intent.action.CANCEL_NOTIFICATION"
        private const val EXTRA_NOTIFICATION_ID = BuildConfig.APPLICATION_ID + ".extra.ID"

        private fun getIntent(context: Context, id: Int): Intent {
            return Intent(context, CancelNotificationReceiver::class.java).apply {
                action = ACTION_CANCEL_NOTIFICATION
                putExtra(EXTRA_NOTIFICATION_ID, id)
            }
        }

        fun getCancelNotificationAction(context: Context, notificationId: Int): Action {
            val appCtx = context.applicationContext
            val intent = getIntent(appCtx, notificationId)
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val pendingIntent = PendingIntent.getBroadcast(appCtx, notificationId, intent, flags)
            return Action(
                R.drawable.ic_notification_clear,
                appCtx.getString(R.string.notification_action_cancel),
                pendingIntent
            )
        }

    }


    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_CANCEL_NOTIFICATION) {
            val id = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
            if (id != 0) NotificationManagerCompat.from(context).cancel(id)
        }
    }

}
