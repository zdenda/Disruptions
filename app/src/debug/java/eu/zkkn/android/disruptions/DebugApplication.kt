package eu.zkkn.android.disruptions

import android.annotation.SuppressLint
import android.os.StrictMode
import com.facebook.stetho.Stetho


// class is used and registered for debug build
@Suppress("unused")
@SuppressLint("Registered")
class DebugApplication : MainApplication() {

    override fun onCreate() {
        super.onCreate()
        enableStrictMode()
        Stetho.initializeWithDefaults(this)
    }

    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build()
        )
    }

}
