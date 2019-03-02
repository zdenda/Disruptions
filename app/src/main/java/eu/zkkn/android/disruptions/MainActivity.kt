package eu.zkkn.android.disruptions

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

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

        btSubscribe.setOnClickListener {
            val line = tiLine.editText?.text.toString()
            FirebaseMessaging.getInstance().subscribeToTopic(topic(line))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Preferences.addTopic(this, line.toUpperCase())
                        tiLine.editText?.text?.clear()
                        refreshSubscriptions()
                    }
                    Toast.makeText(baseContext,
                        if (task.isSuccessful) "Přihlášeno" else "Chyba",
                        Toast.LENGTH_LONG)
                    .show()
                }
        }

        btUnsubscribe.setOnClickListener {
            val line = tiLine.editText?.text.toString()
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic(line))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Preferences.removeTopic(this, line.toUpperCase())
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
        tvChannels.text = "Přihlášeno: ${Preferences.getTopics(this).sorted()}"
    }

    private fun topic(line: String): String {
        // remove spaces a use lower case for the name of line
        return "topic_pid_${line.trim().toLowerCase()}"
    }

}
