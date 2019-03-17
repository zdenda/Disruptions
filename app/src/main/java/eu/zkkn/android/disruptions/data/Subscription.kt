package eu.zkkn.android.disruptions.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "subscription")
data class Subscription(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "line_name")
    val lineName: String

)
