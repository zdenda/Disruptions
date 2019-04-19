package eu.zkkn.android.disruptions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eu.zkkn.android.disruptions.data.Subscription
import kotlinx.android.synthetic.main.list_item_subscription.view.*


class SubscriptionsAdapter : ListAdapter<Subscription, SubscriptionsAdapter.ViewHolder>(DiffCallback()) {

    private val removeButtonClickListener: View.OnClickListener
    private var onRemoveClickListener: ((String) -> Unit)? = null


    init {
        removeButtonClickListener = View.OnClickListener { v ->
            onRemoveClickListener?.invoke(v.tag as String)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_subscription, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvLine.text = holder.tvLine.context.getString(R.string.line_name, item.lineName)
        with(holder.ibRemove) {
            tag = item.lineName
            setOnClickListener(removeButtonClickListener)
        }
    }

    fun setOnRemoveClickListener(function: (String) -> Unit) {
        onRemoveClickListener = function
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLine: TextView = view.tvLine
        val ibRemove: View = view.ibRemove

        override fun toString(): String {
            return super.toString() + " '" + tvLine.text + "'"
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
