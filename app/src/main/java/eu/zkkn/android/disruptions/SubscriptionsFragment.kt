package eu.zkkn.android.disruptions

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EdgeEffect
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
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
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                rwSubscriptions.scrollToPosition(positionStart)
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
        viewModel.subscriptions.observe(viewLifecycleOwner, Observer<List<Subscription>> { subscriptions ->
            empty.visibility = if (subscriptions.isEmpty()) View.VISIBLE else View.GONE
            adapter.submitList(subscriptions)
        })

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

    }

    private fun onSubscribeClick() {
        val lineName = tiLine.editText?.text.toString()
        if (!lineName.isBlank()) {
            viewModel.addSubscription(lineName)
            tiLine.editText?.text?.clear()
        }
    }

}
