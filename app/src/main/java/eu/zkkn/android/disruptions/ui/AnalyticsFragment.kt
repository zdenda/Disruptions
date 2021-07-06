package eu.zkkn.android.disruptions.ui

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import eu.zkkn.android.disruptions.utils.Analytics


open class AnalyticsFragment : Fragment {

    constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    override fun onResume() {
        super.onResume()
        Analytics.sendScreenView(this.javaClass.simpleName, requireActivity())
    }

}
