package eu.zkkn.android.disruptions

import android.app.Application
import androidx.fragment.app.strictmode.FragmentStrictMode
import com.google.firebase.crashlytics.FirebaseCrashlytics
import eu.zkkn.android.disruptions.utils.Analytics


open class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Analytics.init(this)
        enableFragmentsStrictMode()
    }

    private fun enableFragmentsStrictMode() {
        // https://developer.android.com/guide/fragments/debugging#strictmode
        FragmentStrictMode.defaultPolicy =
            FragmentStrictMode.Policy.Builder()
                .detectFragmentReuse()
                .detectFragmentTagUsage()
                .detectRetainInstanceUsage()
                .detectSetUserVisibleHint()
                .detectTargetFragmentUsage()
                .detectWrongFragmentContainer()
                .apply {
                    if (BuildConfig.DEBUG) {
                        // Fail early on DEBUG builds
                        penaltyDeath()
                    } else {
                        // Log to Crashlytics on RELEASE builds
                        penaltyListener {
                            FirebaseCrashlytics.getInstance().recordException(it)
                        }
                    }
                }
                .build()

    }
}
