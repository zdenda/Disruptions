package eu.zkkn.android.disruptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import eu.zkkn.android.disruptions.data.Subscription
import kotlinx.android.synthetic.main.fragment_subscriptions.*


class SubscriptionsFragment : Fragment() {

    companion object {
        private val TAG = this::class.simpleName
    }


    private lateinit var viewModel: SubscriptionsViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_subscriptions, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(SubscriptionsViewModel::class.java)

        val adapter = SubscriptionsAdapter()
        adapter.setOnRemoveClickListener { lineName ->
            viewModel.removeSubscription(lineName)
        }
        rwSubscriptions.adapter = adapter
        rwSubscriptions.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        viewModel.subscriptions.observe(viewLifecycleOwner, Observer<List<Subscription>> { subscriptions ->
            empty.visibility = if (subscriptions.isEmpty()) View.VISIBLE else View.GONE
            adapter.submitList(subscriptions)
        })

        btSubscribe.setOnClickListener {
            viewModel.addSubscription(tiLine.editText?.text.toString())
            tiLine.editText?.text?.clear()
        }
    }

}
