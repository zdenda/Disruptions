package eu.zkkn.android.disruptions

import android.app.Application
import com.facebook.stetho.Stetho


@Suppress("unused") // it's used for debug builds
class DebugApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }

}
