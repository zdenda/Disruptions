package eu.zkkn.android.disruptions.utils

import androidx.room.TypeConverter
import java.util.Date


private const val SEPARATOR = '\u001f' //ASCII US (unit separator)


class RoomTypeConverters {

    @TypeConverter
    fun dateFromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun stringSetFromString(value: String?): Set<String> {
        return value?.removePrefix("$SEPARATOR")
            ?.removeSuffix("$SEPARATOR")
            ?.split(SEPARATOR)?.toSet()
            ?: emptySet()
    }

    @TypeConverter
    fun stringSetToString(set: Set<String>): String {
        return set.joinToString("$SEPARATOR", "$SEPARATOR", "$SEPARATOR")
    }

}
