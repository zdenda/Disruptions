package eu.zkkn.android.disruptions.ui.disruptionlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import eu.zkkn.android.disruptions.R
import eu.zkkn.android.disruptions.ui.AnalyticsFragment
import kotlinx.android.synthetic.main.fragment_disruptions.*


class DisruptionListFragment : AnalyticsFragment() {

    private val viewModel: DisruptionListViewModel by viewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_disruptions, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = DisruptionAdapter()

        with(rwDisruptions) {
            this.adapter = adapter
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        btToSubscriptions.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.subscriptionsFragment))


        viewModel.disruptions.observe(viewLifecycleOwner, { disruptions ->
            empty.visibility = if (disruptions.isEmpty()) View.VISIBLE else View.GONE
            adapter.submitList(disruptions)
        })
    }

}
