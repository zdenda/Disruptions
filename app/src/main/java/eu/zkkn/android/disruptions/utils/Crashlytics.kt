package eu.zkkn.android.disruptions.utils

import com.google.firebase.crashlytics.FirebaseCrashlytics


object Crashlytics {

    fun logException(throwable: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    fun log(message: String) {
        FirebaseCrashlytics.getInstance().log(message)
    }

}
