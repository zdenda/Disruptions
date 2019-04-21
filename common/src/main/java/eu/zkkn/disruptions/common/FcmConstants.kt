package eu.zkkn.disruptions.common

import androidx.annotation.StringDef


object FcmConstants {

    const val KEY_TYPE = "type"
    const val KEY_ID = "id"
    const val KEY_TITLE = "title"
    const val KEY_TIME = "time"
    const val KEY_LINES = "lines"


    @Retention(AnnotationRetention.SOURCE)
    @StringDef(TYPE_NOTIFICATION)
    annotation class FcmMessageType
    const val TYPE_NOTIFICATION = "notification"


    fun topicNameForLine(lineName: String): String {
        // remove spaces a use lower case for the name of line
        return "topic_pid_${lineName.trim().toLowerCase()}"
    }

}
