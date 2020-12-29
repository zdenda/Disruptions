package eu.zkkn.android.disruptions.ui.subscriptionlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eu.zkkn.android.disruptions.R
import eu.zkkn.android.disruptions.data.Subscription
import eu.zkkn.android.disruptions.databinding.ListItemSubscriptionBinding


class SubscriptionAdapter : ListAdapter<Subscription, SubscriptionAdapter.ViewHolder>(DiffCallback()) {

    private val removeButtonClickListener: View.OnClickListener
    private var onRemoveClickListener: ((String) -> Unit)? = null


    init {
        removeButtonClickListener = View.OnClickListener { v ->
            onRemoveClickListener?.invoke(v.tag as String)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemSubscriptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvLine.text = tvLine.context.getString(R.string.line_name, item.lineName)
            with(ibRemove) {
                tag = item.lineName
                setOnClickListener(removeButtonClickListener)
            }
        }
    }

    fun setOnRemoveClickListener(function: (String) -> Unit) {
        onRemoveClickListener = function
    }

    inner class ViewHolder(val binding: ListItemSubscriptionBinding) : RecyclerView.ViewHolder(binding.root) {

        override fun toString(): String {
            return super.toString() + " '" + binding.tvLine.text + "'"
        }

    }

    private class DiffCallback : DiffUtil.ItemCallback<Subscription>() {

        override fun areItemsTheSame(oldItem: Subscription, newItem: Subscription): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Subscription, newItem: Subscription): Boolean {
            return oldItem == newItem
        }
    }

}
