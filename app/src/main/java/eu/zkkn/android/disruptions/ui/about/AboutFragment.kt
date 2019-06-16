package eu.zkkn.android.disruptions.ui.about

import android.app.AlertDialog
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import eu.zkkn.android.disruptions.BuildConfig
import eu.zkkn.android.disruptions.R
import eu.zkkn.android.disruptions.data.Preferences
import kotlinx.android.synthetic.main.fragment_about.view.*


class AboutFragment : Fragment() {

    companion object {
        private val TAG = AboutFragment::class.simpleName
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        view.tvVersion.text = getString(R.string.version, BuildConfig.VERSION_NAME)
        view.tvPrivacyPolicyLink.movementMethod = LinkMovementMethod.getInstance()
        view.tvSourceCodeLink.movementMethod = LinkMovementMethod.getInstance()
        view.tvRopidLink.movementMethod = LinkMovementMethod.getInstance()

        // show debug window on short and long click on app logo
        view.ivAppLogo.setOnClickListener {
            it.setOnLongClickListener {
                showDebugInfo()
                return@setOnLongClickListener true
            }
        }

        return view
    }

    private fun showDebugInfo() {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.dialog_debug_title)
            setMessage(getString(R.string.dialog_debug_last_subscriptions_refresh,
                Preferences.getLastSubscriptionRefreshTime(context)))
            setPositiveButton(R.string.dialog_debug_ok) { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

}
