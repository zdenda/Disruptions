package eu.zkkn.android.disruptions.ui.disruptiondetail

import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.navArgs
import eu.zkkn.android.disruptions.R
import eu.zkkn.android.disruptions.databinding.FragmentDisruptionBinding
import eu.zkkn.android.disruptions.ui.AnalyticsFragment


class DisruptionDetailFragment : AnalyticsFragment(R.layout.fragment_disruption) {

    private val args by navArgs<DisruptionDetailFragmentArgs>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(FragmentDisruptionBinding.bind(view)) {

            // Add Refresh action to the Top App Bar
            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_disruption_detail, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.refresh -> {
                            webView.reload()
                            true
                        }
                        else -> false
                    }
                }
            }, viewLifecycleOwner)


            swipeRefresh.setColorSchemeResources(R.color.colorAccent)
            swipeRefresh.setOnRefreshListener {
                webView.reload()
            }

            webView.webViewClient = object : WebViewClient() {

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    if (!swipeRefresh.isRefreshing) progress.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    if (swipeRefresh.isRefreshing) swipeRefresh.isRefreshing = false
                    progress.visibility = View.GONE
                }

            }

            webView.loadUrl("https://pid.cz/mimoradnost/?id=${args.guid}")
        }
    }

}
