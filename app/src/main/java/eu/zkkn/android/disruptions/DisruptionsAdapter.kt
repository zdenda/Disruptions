package eu.zkkn.android.disruptions

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eu.zkkn.android.disruptions.data.Disruption
import kotlinx.android.synthetic.main.list_item_disruption.view.*


class DisruptionsAdapter : ListAdapter<Disruption, DisruptionsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_disruption, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder) {
            tvLinesLabel.apply {
                text = resources.getQuantityText(R.plurals.label_lines, item.lineNames.size)
            }
            tvLines.text = item.lineNames.joinToString()
            tvReceived.apply {
                text = DateUtils.getRelativeDateTimeString(context, item.received.time, DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.DAY_IN_MILLIS, 0)
            }
            tvTitle.text = item.title.capitalize()
            tvTimeInfo.text = item.timeInfo
        }
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLinesLabel: TextView = view.tvLinesLabel
        val tvLines: TextView = view.tvLines
        val tvReceived: TextView = view.tvReceived
        val tvTitle: TextView = view.tvTitle
        val tvTimeInfo: TextView = view.tvTimeInfo
    }


    private class DiffCallback: DiffUtil.ItemCallback<Disruption>() {

        override fun areItemsTheSame(oldItem: Disruption, newItem: Disruption): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Disruption, newItem: Disruption): Boolean {
            return oldItem == newItem
        }

    }

}
