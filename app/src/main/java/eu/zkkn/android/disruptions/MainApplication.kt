package eu.zkkn.android.disruptions

import android.app.Application
import eu.zkkn.android.disruptions.utils.Analytics


open class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Analytics.init(this)
    }

}
