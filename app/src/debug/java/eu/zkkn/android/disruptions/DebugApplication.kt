package eu.zkkn.android.disruptions

import android.annotation.SuppressLint
import com.facebook.stetho.Stetho


// class is used and registered for debug build
@Suppress("unused")
@SuppressLint("Registered")
class DebugApplication : MainApplication() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }

}
