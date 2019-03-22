package eu.zkkn.android.disruptions

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.subscriptions_fragment.*


class SubscriptionsFragment : Fragment() {

    companion object {
        private val TAG = SubscriptionsFragment::class.simpleName

        fun newInstance() = SubscriptionsFragment()
    }


    private lateinit var viewModel: SubscriptionsViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.subscriptions_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(SubscriptionsViewModel::class.java)

        viewModel.subscriptions.observe(this, Observer<Set<String>> { lineNames ->
            Log.d(TAG, "Change in subscriptions: ${lineNames.joinToString()}")
            tvChannels.text = if (lineNames.isEmpty()) {
                getString(R.string.subscriptions_empty)
            } else {
                getString(R.string.subscriptions, lineNames.joinToString())
            }
        })

        btSubscribe.setOnClickListener {
            viewModel.addSubscription(tiLine.editText?.text.toString())
            tiLine.editText?.text?.clear()
        }

        btUnsubscribe.setOnClickListener {
            viewModel.removeSubscription(tiLine.editText?.text.toString())
            tiLine.editText?.text?.clear()
        }
    }

}
