package eu.zkkn.android.disruptions.ui.about

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import eu.zkkn.android.disruptions.BuildConfig
import eu.zkkn.android.disruptions.R
import eu.zkkn.android.disruptions.data.Preferences
import eu.zkkn.android.disruptions.databinding.FragmentAboutBinding
import eu.zkkn.android.disruptions.ui.AnalyticsFragment
import eu.zkkn.android.disruptions.utils.Analytics
import eu.zkkn.android.disruptions.utils.Helpers


//TODO: Add in-app review https://android-developers.googleblog.com/2020/08/in-app-review-api.html
class AboutFragment : AnalyticsFragment() {

    companion object {
        private const val SHARE_LINK = "https://disruptions.page.link/Hi"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val viewBinding = FragmentAboutBinding.inflate(inflater, container, false)

        with(viewBinding) {
            tvVersion.text = getString(R.string.version, BuildConfig.VERSION_NAME)
            tvPrivacyPolicyLink.movementMethod = LinkMovementMethod.getInstance()
            tvSourceCodeLink.movementMethod = LinkMovementMethod.getInstance()
            tvRopidLink.movementMethod = LinkMovementMethod.getInstance()

            tvOssLicenses.apply {
                paint.isUnderlineText = true
                setOnClickListener {
                    OssLicensesMenuActivity.setActivityTitle(getString(R.string.oss_licenses))
                    startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
                }
            }

            // show debug window on short and long click on app logo
            ivAppLogo.setOnClickListener {
                it.setOnLongClickListener {
                    showDebugInfo()
                    return@setOnLongClickListener true
                }
            }

            btShare.apply {
                text = resources.getStringArray(R.array.button_share_alternatives).random()
                setOnClickListener {
                    Analytics.logShare(SHARE_LINK)
                    showShareDialog()
                }
            }
        }

        return viewBinding.root

    }

    private fun showShareDialog() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, SHARE_LINK)
        startActivity(Intent.createChooser(intent, getString(R.string.button_share)))
    }

    private fun showDebugInfo() {
        // or MaterialAlertDialogBuilder could be also used
        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.dialog_debug_title)
            setMessage(getString(R.string.dialog_debug_message,
                Preferences.getLastSubscriptionRefreshTime(context),
                Preferences.getLastHeartbeatReceivedTime(context),
                Preferences.getLastHeartbeatSentTime(context),
                Helpers.getAppStandbyBucket(context),
            ))
            setPositiveButton(R.string.dialog_debug_ok) { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

}
