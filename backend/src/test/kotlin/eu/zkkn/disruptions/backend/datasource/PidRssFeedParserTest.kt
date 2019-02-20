package eu.zkkn.disruptions.backend.datasource

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime


class PidRssFeedParserTest {

    @Test
    fun parse_title() {
        val xml = "<rss><channel><title> Mimořádnosti </title><lastBuildDate></lastBuildDate></channel></rss>"
        val feed = PidRssFeedParser(xml.byteInputStream()).parse()

        assertEquals("Mimořádnosti", feed.title)
    }

    @Test
    fun parse_title_header() {
        val xml = """
            <rss>
                <channel>
                    <title>Mimořádnosti</title>
                    <lastBuildDate />
                    <item>
                        <title>Provoz omezen</title>
                        <guid></guid>
                        <description></description>
                    </item>
                </channel>
            </rss>""".trimIndent()
        val feed = PidRssFeedParser(xml.byteInputStream()).parse()

        assertEquals("Mimořádnosti", feed.title)
    }

    @Test
    fun parse_updated() {
        val xml = """
            <rss>
                <channel>
                    <title>Pražská integrovaná doprava - Mimořádnosti</title>
                    <lastBuildDate>Sun, 03 Feb 2019 16:57:19 +0000</lastBuildDate>
                </channel>
            </rss>""".trimIndent()
        val feed = PidRssFeedParser(xml.byteInputStream()).parse()

        assertEquals(LocalDateTime.of(2019, 2, 3, 16, 57, 19), feed.updated)
    }

    @Test
    fun parse_itemGuid() {
        val xml = """
            <rss>
                <channel>
                    <title>Mimořádnosti</title>
                    <lastBuildDate />
                    <item><title>Provoz omezen</title><guid>5998-1</guid><description /></item>
                </channel>
            </rss>""".trimIndent()
        val feed = PidRssFeedParser(xml.byteInputStream()).parse()

        assertEquals("5998-1", feed.items[0].guid)
    }

    @Test
    fun parse_itemTitle() {
        val xml = """
            <rss><channel>
                <title>Mimořádnosti</title>
                <lastBuildDate />
                <item>
                    <title>Provoz omezen</title>
                    <guid>5998-1</guid>
                    <description><![CDATA[]]></description>
                </item>
            </channel></rss>""".trimIndent()
        val feed = PidRssFeedParser(xml.byteInputStream()).parse()

        assertEquals("Provoz omezen", feed.items[0].title)
    }


    @Test
    fun parse_itemDescriptionCdata() {
        val xml = """
            <rss><channel><title>Mimořádnosti</title><lastBuildDate />
                <item>
                    <title>Provoz omezen</title>
                    <guid>5998-1</guid>
                    <description><![CDATA[31.12. 23:59 - do&nbsp;odvolání; Dotčené linky: A, 1, 100, LD, P1, AE]]></description>
                </item>
            </channel></rss>""".trimIndent()
        val feed = PidRssFeedParser(xml.byteInputStream()).parse()

        assertEquals("31.12. 23:59 - do odvolání", feed.items[0].timeInfo)
        assertEquals(6, feed.items[0].lines.size)
        assertTrue(feed.items[0].lines.containsAll(listOf("AE", "P1", "LD", "100", "1", "A")))
    }

    @Test
    fun parse_full() {
        val xml = """
            <rss>
                <channel>
                    <title>Pražská integrovaná doprava - Mimořádnosti</title>
                    <lastBuildDate>Mon, 04 Feb 2019 09:45:14 +0000</lastBuildDate>
                    <item>
                        <title>Kavalírka - Klamovka - Provoz omezen</title>
                        <guid isPermaLink="false">5998-1</guid>
                        <description><![CDATA[4.2. 09:00 - do&nbsp;odvolání; Dotčené linky: 9, 10, 15, 16]]></description>
                    </item>
                    <item>
                        <title>
                            Letňany - Palmovka (trolejbus linky 58, oběma směry) - Provoz zastaven, Neodjetí spoje
                        </title>
				        <guid isPermaLink="false">5999-1</guid>
                        <description><![CDATA[4.2. 09:08 - do&nbsp;odvolání; Dotčené linky: 58]]></description>
                    </item>
                </channel>
            </rss>""".trimIndent()
        val feed = PidRssFeedParser(xml.byteInputStream()).parse()

        assertEquals("Pražská integrovaná doprava - Mimořádnosti", feed.title)
        assertEquals(LocalDateTime.of(2019, 2, 4, 9, 45, 14), feed.updated)
        assertTrue(feed.items.contains(
            PidRssFeed.Item(
                guid = "5999-1",
                title = "Letňany - Palmovka (trolejbus linky 58, oběma směry) - Provoz zastaven, Neodjetí spoje",
                timeInfo = "4.2. 09:08 - do odvolání",
                lines = listOf("58")
            )
        ))
        assertTrue(feed.items.contains(
            PidRssFeed.Item(
                guid = "5998-1",
                title = "Kavalírka - Klamovka - Provoz omezen",
                timeInfo = "4.2. 09:00 - do odvolání",
                lines = listOf("9", "10", "15", "16")
            )
        ))
    }

    @Test
    fun parseDescription() {
        val (timeInfo, lines) = PidRssFeedParser("".byteInputStream())
            .parseDescription("4.2. 18:53 - do&nbsp;odvolání; Dotčené linky: 9, 10, 15, 16")

        assertEquals("4.2. 18:53 - do odvolání", timeInfo)
        assertEquals(4, lines.size)
        assertTrue(lines.containsAll(listOf("9", "10", "15", "16")))
    }

    @Test
    fun parseDescription_end() {
        val (timeInfo, lines) = PidRssFeedParser("".byteInputStream())
            .parseDescription("4.2. 14:35 - 4.2. 16:51; Dotčené linky: 464")

        assertEquals("4.2. 14:35 - 4.2. 16:51", timeInfo)
        assertEquals(1, lines.size)
        assertTrue(lines.containsAll(listOf("464")))
    }

    @Test
    fun parseDescription_no_lines() {
        val (timeInfo, lines) = PidRssFeedParser("".byteInputStream())
            .parseDescription("4.2. 18:53 - do&nbsp;odvolání; Dotčené linky: ")

        assertEquals("4.2. 18:53 - do odvolání", timeInfo)
        assertEquals(0, lines.size)
        assertTrue(lines.isEmpty())
    }

    @Test
    fun parseDescription_no_dates() {
        val (timeInfo, lines) = PidRssFeedParser("".byteInputStream())
            .parseDescription("provoz obnoven; Dotčené linky: S34")

        assertEquals("provoz obnoven", timeInfo)
        assertEquals(1, lines.size)
        assertTrue(lines.containsAll(listOf("S34")))
    }

}
