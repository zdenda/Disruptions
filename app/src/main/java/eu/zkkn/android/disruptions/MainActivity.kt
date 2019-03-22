package eu.zkkn.android.disruptions

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.simpleName
    }

    private val subscriptionsFragment by lazy { SubscriptionsFragment.newInstance() }
    private val aboutFragment by lazy { AboutFragment.newInstance() }
    private var currentFragment: Fragment = subscriptionsFragment

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                supportFragmentManager.beginTransaction()
                    .hide(currentFragment).show(subscriptionsFragment)
                    .commit()
                currentFragment = subscriptionsFragment
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_about -> {
                supportFragmentManager.beginTransaction()
                    .hide(currentFragment).show(aboutFragment)
                    .commit()
                currentFragment = aboutFragment
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        if (savedInstanceState == null) {
            //TODO: doesn't work on screen rotation
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment, aboutFragment).hide(aboutFragment)
                .commit()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment, subscriptionsFragment)
                .commit()
        }


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
