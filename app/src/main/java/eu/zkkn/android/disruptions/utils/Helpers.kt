package eu.zkkn.android.disruptions.utils

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build


object Helpers {

    fun getAppStandbyBucket(ctx: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val usageStats = ctx.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

            /*
            val queryEventsForSelf = usageStats.queryEventsForSelf(0, System.currentTimeMillis())
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val event = UsageEvents.Event()
            while (queryEventsForSelf.hasNextEvent()) {
                queryEventsForSelf.getNextEvent(event)
                val res = "${dateFormat.format(Date(event.timeStamp))} ${event.eventType} ${event.appStandbyBucket}"
                Log.d("ZKLog", res)
            }
            */

            return usageStats.appStandbyBucket
        } else {
            -1
        }
    }

}
