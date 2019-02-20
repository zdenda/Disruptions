package eu.zkkn.disruptions.backend.datasource

import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import javax.xml.stream.XMLInputFactory


private const val TITLE = "title"
private const val LAST_BUILD_DATE = "lastBuildDate"
private const val ITEM = "item"
private const val GUID = "guid"
private const val DESCRIPTION = "description"


class PidRssFeedParser(private val input: InputStream) {

    companion object {
        const val URL = "https://pid.cz/feed/rss-mimoradnosti"
    }

    private val dateTimePattern = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss Z", Locale.US)

    fun parse(): PidRssFeed {

        lateinit var title: String
        lateinit var lastBuildDate: LocalDateTime
        val items = mutableListOf<PidRssFeed.Item>()
        lateinit var itemGuid: String
        lateinit var itemTitle: String
        lateinit var itemTimeInfo: String
        lateinit var itemLines: List<String>
        var inItem = false
        var characters = ""

        val xmlInputFactory = XMLInputFactory.newInstance()
        xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, true)
        xmlInputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, true)
        val eventReader = xmlInputFactory.createXMLEventReader(input)

        while (eventReader.hasNext()) {
            val event = eventReader.nextEvent()

            when {

                event.isStartElement -> {
                    val elementName = event.asStartElement().name.localPart
                    when (elementName) {
                        ITEM -> inItem = true
                    }
                }

                event.isEndElement -> {
                    val elementName = event.asEndElement().name.localPart
                    when (elementName) {
                        TITLE -> {
                            if (!inItem) title = characters else itemTitle = characters
                            characters = ""
                        }
                        LAST_BUILD_DATE -> {
                            lastBuildDate = try {
                                LocalDateTime.parse(characters, dateTimePattern)
                            } catch (e: DateTimeParseException) {
                                LocalDateTime.now()
                            }
                            characters = ""
                        }
                        ITEM -> {
                            items.add(PidRssFeed.Item(itemGuid, itemTitle, itemTimeInfo, itemLines))
                            inItem = false
                        }
                        GUID -> {
                            itemGuid = characters
                            characters = ""
                        }
                        DESCRIPTION -> {
                            val (timeInfo, lines) = parseDescription(characters)
                            itemTimeInfo = timeInfo
                            itemLines = lines
                            characters = ""
                        }
                    }

                }

                event.isCharacters -> characters = event.asCharacters().data.trim()
            }
        }

        return PidRssFeed(title, lastBuildDate, items)

    }


    internal fun parseDescription(description: String): Pair<String, List<String>> {
        if (description.isEmpty()) return Pair("", emptyList())

        val lastSemicolon = description.lastIndexOf(';')
        val timeInfo = description.substring(0, lastSemicolon).replace("&nbsp;", " ")
        val lines = parseDescLines(description.substring(lastSemicolon + 1))
        return Pair(timeInfo, lines)
    }

    private fun parseDescLines(text: String): List<String> {
        return text.substring(text.indexOf(':') + 1) // take text after first colon
            .split(',') // split it by commas
            .map { n -> n.trim() } // remove leading and trailing spaces
            .filterNot { it.isBlank() } // and return only not blank values
    }

    /*
    private fun parseDescFromAndTo(text: String): Pair<LocalDateTime, LocalDateTime> {
        /*
        val dateTimeFormatter = DateTimeFormatterBuilder().appendPattern("d.M. HH:mm")
            .parseDefaulting(ChronoField.YEAR_OF_ERA, LocalDateTime.now().year.toLong())
            .toFormatter()
        LocalDateTime.parse(..., dateTimeFormatter)
        */
        return Pair(LocalDateTime.now(), LocalDateTime.now())
    }
    */

}
