package eu.zkkn.android.disruptions.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface DisruptionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(disruption: Disruption): Long

    @Query("SELECT * FROM disruption ORDER BY received DESC, id DESC")
    fun getAll(): LiveData<List<Disruption>>

}
