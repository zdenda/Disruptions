package eu.zkkn.android.disruptions.data

import android.content.Context
import androidx.lifecycle.LiveData
import eu.zkkn.android.disruptions.utils.ioThread
import java.util.Date

class DisruptionRepository private constructor(private val dao: DisruptionDao) {

    companion object {
        // For Singleton instantiation
        @Volatile
        private var INSTANCE: DisruptionRepository? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: DisruptionRepository(AppDatabase.getInstance(context).disruptionDao())
                    .also { INSTANCE = it }
            }
    }


    fun getDisruptions(): LiveData<List<Disruption>> = dao.getAll()

    //TODO: prevent inserting two same guid
    fun addDisruption(
        guid: String,
        lines: Set<String>,
        title: String,
        timeInfo: String,
        received: Date = Date()
    ) = ioThread { dao.insert(Disruption(0, guid, received, lines, title, timeInfo)) }

}
