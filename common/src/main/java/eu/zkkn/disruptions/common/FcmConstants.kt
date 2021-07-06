package eu.zkkn.disruptions.common

import androidx.annotation.StringDef
import java.text.Normalizer


object FcmConstants {

    private const val TOPIC_PREFIX = "topic"

    const val KEY_TYPE = "type"
    const val KEY_ID = "id"
    const val KEY_TITLE = "title"
    const val KEY_TIME = "time"
    const val KEY_LINES = "lines"


    @Retention(AnnotationRetention.SOURCE)
    @StringDef(TYPE_NOTIFICATION, TYPE_HEARTBEAT)
    annotation class FcmMessageType
    const val TYPE_NOTIFICATION = "notification"
    const val TYPE_HEARTBEAT = "heartbeat"

    const val TOPIC_HEARTBEAT = "${TOPIC_PREFIX}_admin_heartbeat"


    fun topicNameForLine(lineName: String): String {
        // remove spaces a use lower case for the name of line
        return "${TOPIC_PREFIX}_pid_${normalizeLineName(lineName)}"
    }


    private fun normalizeLineName(lineName: String): String {
        return lineName.trim()
            .take(40)
            .replace(" ", "-")
            .normalize()
            .lowercase()
    }


    // Removes diacritics (accents) from the string
    private fun String.normalize(): String {
        return Normalizer.normalize(this, Normalizer.Form.NFKD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }

}
