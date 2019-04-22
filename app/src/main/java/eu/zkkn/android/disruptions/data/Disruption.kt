package eu.zkkn.android.disruptions.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date


@Entity(
    tableName = "disruption",
    indices = [Index(value = ["guid"], unique = true),
        Index(value = ["received"])
    ]
)
data class Disruption(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "guid")
    val guid: String,

    @ColumnInfo(name = "received")
    val received: Date,

    @ColumnInfo(name = "line_names")
    val lineNames: Set<String>,

    val title: String,

    @ColumnInfo(name = "time_info")
    val timeInfo: String

)
