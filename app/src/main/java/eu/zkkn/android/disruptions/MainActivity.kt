package eu.zkkn.android.disruptions

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.messaging.FirebaseMessaging
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

        navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.subscriptionsFragment, R.id.disruptionsFragment, R.id.aboutFragment)
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
