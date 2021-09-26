package eu.zkkn.android.disruptions.utils

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build


object Helpers {

    fun getAppStandbyBucket(ctx: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val usageStats = ctx.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            return usageStats.appStandbyBucket
        } else {
            -1
        }
    }

}
