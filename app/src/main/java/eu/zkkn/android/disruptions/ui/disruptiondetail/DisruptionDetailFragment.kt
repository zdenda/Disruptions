package eu.zkkn.android.disruptions.ui.disruptiondetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_disruption.*
import android.graphics.Bitmap
import android.webkit.WebView
import eu.zkkn.android.disruptions.R


class DisruptionDetailFragment : Fragment() {

    private val args by navArgs<DisruptionDetailFragmentArgs>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_disruption, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
