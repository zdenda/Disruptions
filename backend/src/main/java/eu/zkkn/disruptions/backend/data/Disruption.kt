package eu.zkkn.disruptions.backend.data

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import com.googlecode.objectify.annotation.Index
import eu.zkkn.disruptions.backend.datasource.PidRssFeed
import java.lang.RuntimeException
import java.util.Date


@Entity
data class Disruption(

    @Id
    var id: Long?,

    @Index
    var guid: String,

    var lines: MutableSet<String>,
    var title: String,
    var timeInfo: String,
    var created: Date,
    var updated: Date?

) {

    @Suppress("unused") // Objectify needs a no-arg constructor
    private constructor() : this(null, "", mutableSetOf<String>(), "", "", Date(), null)

    fun modify(feedItem: PidRssFeed.Item) {
        if (guid != feedItem.guid) {
            throw RuntimeException("Cannot modify disruption with a different GUID ($guid != ${feedItem.guid})")
        }
        lines = feedItem.lines.toMutableSet()
        title = feedItem.title
        timeInfo = feedItem.timeInfo
        updated = Date()
    }


    companion object {

        fun fromPidRssFeedItem(feedItem: PidRssFeed.Item, created: Date = Date()): Disruption {
            return Disruption(null, feedItem.guid, feedItem.lines.toMutableSet(), feedItem.title, feedItem.timeInfo,
                created, null)
        }

    }

}
