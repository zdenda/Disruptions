package eu.zkkn.android.disruptions

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_about.view.*


class AboutFragment : Fragment() {

    companion object {
        private val TAG = AboutFragment::class.simpleName
        fun newInstance() = AboutFragment()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        view.tvVersion.text = getString(R.string.version, BuildConfig.VERSION_NAME)
        view.tvPrivacyPolicyLink.movementMethod = LinkMovementMethod.getInstance()
        view.tvSourceCodeLink.movementMethod = LinkMovementMethod.getInstance()
        view.tvRopidLink.movementMethod = LinkMovementMethod.getInstance()

        return view
    }

}
