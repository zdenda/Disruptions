package eu.zkkn.android.disruptions

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.simpleName
    }


    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.subscriptionsFragment, R.id.disruptionsFragment, R.id.aboutFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavigation.setupWithNavController(navController)

        //TODO check play services
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Firebase getInstanceId() failed", task.exception)
                return@addOnCompleteListener
            }
            Log.d(TAG, "Firebase token: ${task.result?.token}")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}
