package eu.zkkn.android.disruptions

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.simpleName
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                //TODO
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                //TODO
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                //TODO
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        //TODO check play services
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Firebase getInstanceId() failed", task.exception)
                return@addOnCompleteListener
            }
            Log.d(TAG, "Firebase token: ${task.result?.token}")
        }
    }

}
