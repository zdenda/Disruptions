package eu.zkkn.android.disruptions.ui.disruptionlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import eu.zkkn.android.disruptions.data.Disruption
import eu.zkkn.android.disruptions.data.DisruptionRepository


class DisruptionListViewModel(application: Application) : AndroidViewModel(application) {

    private val disruptionRepository by lazy { DisruptionRepository.getInstance(application) }

    val disruptions: LiveData<List<Disruption>> by lazy {
        disruptionRepository.getDisruptions()
    }

}
