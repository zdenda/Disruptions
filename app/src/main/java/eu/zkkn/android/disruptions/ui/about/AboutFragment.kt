package eu.zkkn.android.disruptions.ui.about

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import eu.zkkn.android.disruptions.BuildConfig
import eu.zkkn.android.disruptions.R
import eu.zkkn.android.disruptions.data.Preferences
import eu.zkkn.android.disruptions.ui.AnalyticsFragment
import kotlinx.android.synthetic.main.fragment_about.view.*


class AboutFragment : AnalyticsFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {

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

        view.btShare.apply {
            text = resources.getStringArray(R.array.button_share_alternatives).random()
            setOnClickListener { showShareDialog() }
        }


        return view

    }

    private fun showShareDialog() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "https://disruptions.page.link/Hi")
        startActivity(Intent.createChooser(intent, getString(R.string.button_share)))
    }

    private fun showDebugInfo() {
        // or MaterialAlertDialogBuilder could be also used
        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.dialog_debug_title)
            setMessage(getString(R.string.dialog_debug_last_subscriptions_refresh,
                Preferences.getLastSubscriptionRefreshTime(context),
                Preferences.getLastHeartbeatReceivedTime(context),
                Preferences.getLastHeartbeatSentTime(context)))
            setPositiveButton(R.string.dialog_debug_ok) { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

}
