package eu.zkkn.android.disruptions

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import eu.zkkn.android.disruptions.data.Preferences
import eu.zkkn.android.disruptions.databinding.ActivityMainBinding
import eu.zkkn.android.disruptions.utils.ioThread
import eu.zkkn.android.disruptions.workers.RefreshSubscriptionsWorker


class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.simpleName
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 24 * 60 * 60
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.activate().addOnCompleteListener { task ->
            if (task.isSuccessful && task.result) {
                Log.d(TAG, "Changes from remote config were activated")
            }
        }

        if (Preferences.isRealtimePositionsEnabled(this) == false ||
            !remoteConfig.getBoolean("show_realtime_positions")) {
            binding.bottomNavigation.menu.removeItem(R.id.navMap)
        }

        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.subscriptionsFragment, R.id.disruptionsFragment, R.id.aboutFragment, R.id.mapFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)

        //TODO check play services
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            Log.d(TAG, "FCM token: ${task.result}")
        }

        remoteConfig.fetch()

        ioThread {
            if (Preferences.isFirstRun(this)) {
                RefreshSubscriptionsWorker.runRefresh(this)
            }
            if (!Preferences.isPeriodicSubscriptionRefreshEnabled(this)) {
                RefreshSubscriptionsWorker.schedulePeriodicRefresh(this)
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}
