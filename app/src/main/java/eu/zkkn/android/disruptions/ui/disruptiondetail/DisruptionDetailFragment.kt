package eu.zkkn.android.disruptions.ui.disruptiondetail

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import eu.zkkn.android.disruptions.R
import eu.zkkn.android.disruptions.databinding.FragmentDisruptionBinding
import eu.zkkn.android.disruptions.ui.AnalyticsFragment


class DisruptionDetailFragment : AnalyticsFragment(R.layout.fragment_disruption) {

    private val args by navArgs<DisruptionDetailFragmentArgs>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(FragmentDisruptionBinding.bind(view)) {
            webView.webViewClient = object : WebViewClient() {

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    progress.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    progress.visibility = View.GONE
                }

            }

            webView.loadUrl("https://pid.cz/mimoradnost/?id=${args.guid}")
        }
    }

}
