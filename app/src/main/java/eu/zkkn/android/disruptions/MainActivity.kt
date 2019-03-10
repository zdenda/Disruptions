package eu.zkkn.android.disruptions

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import eu.zkkn.android.disruptions.data.SubscriptionRepository
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.simpleName

    private val subscriptionRepository by lazy { SubscriptionRepository.getInstance(this) }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                message.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        refreshSubscriptions()

        //TODO check play services
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Firebase getInstanceId() failed", task.exception)
                return@addOnCompleteListener
            }
            Log.d(TAG, "Firebase token: ${task.result?.token}")
        }


        btSubscribe.setOnClickListener {
            val line = tiLine.editText?.text.toString().trim()
            val topic = topic(line)
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        subscriptionRepository.addSubscription(line)
                        tiLine.editText?.text?.clear()
                        refreshSubscriptions()
                    }
                    Log.d(TAG, "Topic: $topic (subscribed: ${task.isSuccessful})")
                    Toast.makeText(baseContext,
                        if (task.isSuccessful) "Přihlášeno" else "Chyba",
                        Toast.LENGTH_LONG)
                    .show()
                }
        }

        btUnsubscribe.setOnClickListener {
            val line = tiLine.editText?.text.toString().trim()
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic(line))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        subscriptionRepository.removeSubscription(line)
                        tiLine.editText?.text?.clear()
                        refreshSubscriptions()
                    }
                    Toast.makeText(baseContext,
                        if (task.isSuccessful) "Odhlášeno" else "Chyba",
                        Toast.LENGTH_LONG)
                        .show()
                }
        }

    }

    private fun refreshSubscriptions() {
        val topics = subscriptionRepository.getSubscriptions()
        tvChannels.text = if (topics.isEmpty()) {
            "Přihlaste se k odběru upozornění na mimořídnosti v provozu na linkách pražské MHD"
        } else {
            "Přihlášeno: ${topics.joinToString()}"
        }
    }

    private fun topic(line: String): String {
        // remove spaces a use lower case for the name of line
        return "topic_pid_${line.trim().toLowerCase()}"
    }

}
