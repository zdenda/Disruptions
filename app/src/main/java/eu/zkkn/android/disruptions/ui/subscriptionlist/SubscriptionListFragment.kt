package eu.zkkn.android.disruptions.ui.subscriptionlist

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EdgeEffect
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import eu.zkkn.android.disruptions.R
import eu.zkkn.android.disruptions.data.Subscription
import eu.zkkn.android.disruptions.utils.isValidLineName
import kotlinx.android.synthetic.main.fragment_subscriptions.*


class SubscriptionListFragment : Fragment() {

    companion object {
        private val TAG = SubscriptionListFragment::class.simpleName
    }


    private val viewModel: SubscriptionListViewModel by viewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_subscriptions, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = SubscriptionAdapter()
        adapter.setOnRemoveClickListener { lineName ->
            viewModel.removeSubscription(lineName)
        }
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                rwSubscriptions.smoothScrollToPosition(positionStart)
            }
        })


        // change color for scroll edge effect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rwSubscriptions.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
                override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                    val context = view.context
                    return EdgeEffect(context).apply {
                        color = if (direction == DIRECTION_BOTTOM) {
                            ContextCompat.getColor(context, R.color.backgroundSecondary)
                        } else {
                            ContextCompat.getColor(context, R.color.colorPrimary)
                        }
                    }
                }
            }
        }
        rwSubscriptions.adapter = adapter
        rwSubscriptions.setHasFixedSize(true)
        rwSubscriptions.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        tiLine.editText?.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT -> {
                    onSubscribeClick()
                    true
                }
                else -> false
            }
        }

        btSubscribe.setOnClickListener { onSubscribeClick() }

        viewModel.subscriptions.observe(viewLifecycleOwner, Observer<List<Subscription>> { subscriptions ->
            empty.visibility = if (subscriptions.isEmpty()) View.VISIBLE else View.GONE
            adapter.submitList(subscriptions)
        })

    }


    private fun onSubscribeClick() {
        val lineName = tiLine.editText?.text.toString().trim()
        if (lineName.isNotBlank()) {
            if (lineName.isValidLineName()) {
                tiLine.editText?.text?.clear()
                tiLine.error = null
                viewModel.addSubscription(lineName)
            } else {
                tiLine.error = getString(R.string.input_line_wrong_name)
            }
        }
    }

}
