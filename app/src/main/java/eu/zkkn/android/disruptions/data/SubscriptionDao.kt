package eu.zkkn.android.disruptions.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface SubscriptionDao {

    @Insert
    fun insert(subscription: Subscription)

    @Delete
    fun delete(subscription: Subscription)

    @Query("DELETE FROM subscription WHERE line_name = :lineName COLLATE NOCASE") // Line Name is case insensitive
    fun deleteByLineName(lineName: String)

    @Query("SELECT * FROM subscription ORDER BY id ASC")
    fun getAll(): LiveData<List<Subscription>>

}
