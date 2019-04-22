package eu.zkkn.android.disruptions.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface DisruptionDao {

    @Insert
    fun insert(disruption: Disruption)

    @Query("SELECT * FROM disruption ORDER BY received DESC, id DESC")
    fun getAll(): LiveData<List<Disruption>>

}
