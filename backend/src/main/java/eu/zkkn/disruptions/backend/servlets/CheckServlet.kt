package eu.zkkn.disruptions.backend.servlets

import eu.zkkn.disruptions.backend.MyGson
import eu.zkkn.disruptions.backend.Utils
import eu.zkkn.disruptions.backend.data.Disruption
import eu.zkkn.disruptions.backend.data.DisruptionDao
import eu.zkkn.disruptions.backend.datasource.PidRssFeedParser
import eu.zkkn.disruptions.backend.messaging.Messaging
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.time.TimeSource





@WebServlet(name = "CheckServlet", urlPatterns = ["/check"])
class CheckServlet : HttpServlet() {

    private val log = Logger.getLogger(CheckServlet::class.java.name)


    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {

        val timeSource = TimeSource.Monotonic
        val markStart = timeSource.markNow()

        val inputStream = try {
            Utils.openHttpConnection(PidRssFeedParser.URL).inputStream
        } catch (_: IOException) {
            val elapsed = timeSource.markNow() - markStart
            val backupUrl = PidRssFeedParser.getBackupUrl()
            log.warning("Use secondary URL (${backupUrl}) after ${elapsed.inWholeMilliseconds}ms") //TODO: show only as info
            Utils.openHttpConnection(backupUrl).inputStream
        }

        val pidRssFeed = PidRssFeedParser(inputStream).parse()
        log.config(pidRssFeed.toString())

        val disruptions = DisruptionDao()

        for (item in pidRssFeed.items) {
            val linesToNotify = item.lines.toMutableSet()

            var disruption = disruptions.load(item.guid)
            if (disruption != null) {
                // remove all lines which already received the notification
                linesToNotify.removeAll(disruption.lines)
                disruption.modify(item)
            } else {
                disruption = Disruption.fromPidRssFeedItem(item)
            }

            if (linesToNotify.isNotEmpty()) {
                log.info("Send notifications to: $linesToNotify")
                val results = Messaging.send(Messaging.prepareNotificationMessages(linesToNotify, item))
                log.info(results.toString())
            }

            disruptions.save(disruption)
        }

        resp.contentType = "application/json; charset=UTF-8"
        MyGson.get().toJson(pidRssFeed, resp.writer)

        val elapsed = timeSource.markNow() - markStart
        //TODO: only show if it's above 2 minutes
        log.log(
            // Checking runs every 3 minutes, so each run should finish under 2 minutes
            // to keep some safety margin
            if (elapsed.inWholeMinutes >= 2) Level.SEVERE else Level.CONFIG,
            "Finished after ${elapsed.inWholeMilliseconds}ms"
        )
    }

}
