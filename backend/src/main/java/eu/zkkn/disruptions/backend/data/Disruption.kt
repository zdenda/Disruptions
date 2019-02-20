package eu.zkkn.disruptions.backend.data

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import com.googlecode.objectify.annotation.Index
import eu.zkkn.disruptions.backend.datasource.PidRssFeed
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


    companion object {

        fun fromPidRssFeedItem(feedItem: PidRssFeed.Item, created: Date = Date()): Disruption {
            return Disruption(null, feedItem.guid, feedItem.lines.toMutableSet(), feedItem.title, feedItem.timeInfo,
                created, null)
        }

    }

}
