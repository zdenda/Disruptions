package eu.zkkn.android.disruptions.ui.disruptionlist

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eu.zkkn.android.disruptions.R
import eu.zkkn.android.disruptions.data.Disruption
import eu.zkkn.android.disruptions.databinding.ListItemDisruptionBinding
import eu.zkkn.android.disruptions.utils.capitalize


//TODO: use paging and PagedListAdapter
//https://developer.android.com/topic/libraries/architecture/paging
class DisruptionAdapter : ListAdapter<Disruption, DisruptionAdapter.ViewHolder>(DiffCallback()) {

    private val onItemClickListener = View.OnClickListener { view ->
        view.findNavController().navigate(
            DisruptionListFragmentDirections.actionShowDisruptionDetail(view.tag as String)
        )
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemDisruptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.setOnClickListener(onItemClickListener)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            root.tag = item.guid
            tvLinesLabel.apply {
                text = resources.getQuantityText(R.plurals.label_lines, item.lineNames.size)
            }
            tvLines.text = item.lineNames.joinToString()
            tvReceived.apply {
                text = DateUtils.getRelativeDateTimeString(
                    context, item.received.time, DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.DAY_IN_MILLIS, 0
                )
            }
            tvTitle.text = item.title.capitalize()
            tvTimeInfo.text = item.timeInfo
        }
    }


    inner class ViewHolder(val binding: ListItemDisruptionBinding) : RecyclerView.ViewHolder(binding.root)

    private class DiffCallback : DiffUtil.ItemCallback<Disruption>() {

        override fun areItemsTheSame(oldItem: Disruption, newItem: Disruption): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Disruption, newItem: Disruption): Boolean {
            return oldItem == newItem
        }

    }

}
