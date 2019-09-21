package eu.zkkn.android.disruptions.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import eu.zkkn.android.disruptions.utils.Analytics


open class AnalyticsFragment : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Analytics.sendScreenView(this.javaClass.simpleName, requireActivity())
    }

}
