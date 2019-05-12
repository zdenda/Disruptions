package eu.zkkn.android.disruptions.workers

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import com.google.android.gms.tasks.Tasks
import com.google.firebase.messaging.FirebaseMessaging
import eu.zkkn.android.disruptions.data.Preferences
import eu.zkkn.android.disruptions.data.SubscriptionRepository
import eu.zkkn.disruptions.common.FcmConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit


class RefreshSubscriptionsWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    companion object {

        private const val WORK_NAME = "eu.zkkn.android.disruptions.workers.RefreshSubscriptionsWorker"

        private val TAG = RefreshSubscriptionsWorker::class.simpleName

        internal fun schedulePeriodicRefresh(context: Context) {

            //TODO: use WorkManager.getWorkInfosByTag(WORK_NAME) to check if it's already scheduled
            if (Preferences.isPeriodicSubscriptionRefreshEnabled(context)) return

            Log.d(TAG, "Schedule periodic refresh for FCM topic subscriptions")
            val workManager = WorkManager.getInstance(context)
            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                PeriodicWorkRequestBuilder<RefreshSubscriptionsWorker>(10, TimeUnit.DAYS)
                    .addTag(WORK_NAME)
                    .setBackoffCriteria(
                        BackoffPolicy.EXPONENTIAL,
                        WorkRequest.DEFAULT_BACKOFF_DELAY_MILLIS * 4,
                        TimeUnit.MILLISECONDS
                    )
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.UNMETERED)
                            .setRequiresCharging(true)
                            .build()
                    )
                    .build()
            )
            Preferences.setPeriodicSubscriptionRefresh(context)

        }

    }


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        val lineNames = SubscriptionRepository.getInstance(applicationContext).getAllLineNames()
        Log.d(TAG, "Refreshing subscriptions for lines: ${lineNames.joinToString()}")

        if (lineNames.isEmpty()) return@withContext Result.success()

        val firebaseMessaging = FirebaseMessaging.getInstance()
        var success = true
        for (lineName in lineNames) {
            try {

                val topicName = FcmConstants.topicNameForLine(lineName)
                Tasks.await(firebaseMessaging.subscribeToTopic(topicName))
                Log.d(TAG, "Subscribed to topic: $topicName")

            } catch (e: ExecutionException) {
                success = false
            } catch (e: InterruptedException) {
                success = false
            }
        }

        if (success) Preferences.setLastSubscriptionRefreshTime(applicationContext)

        return@withContext if (success) Result.success() else Result.retry()

    }

}
