package eu.zkkn.android.disruptions.ui.disruptionlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import eu.zkkn.android.disruptions.R
import eu.zkkn.android.disruptions.databinding.FragmentDisruptionsBinding
import eu.zkkn.android.disruptions.ui.AnalyticsFragment


class DisruptionListFragment : AnalyticsFragment(R.layout.fragment_disruptions) {

    private val viewModel: DisruptionListViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(FragmentDisruptionsBinding.bind(view)) {
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

}
