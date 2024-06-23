package eu.zkkn.disruptions.backend.servlets

import eu.zkkn.disruptions.backend.MyGson
import eu.zkkn.disruptions.backend.data.Disruption
import eu.zkkn.disruptions.backend.data.DisruptionDao
import eu.zkkn.disruptions.backend.datasource.PidRssFeedParser
import eu.zkkn.disruptions.backend.messaging.Messaging
import java.io.IOException
import java.net.URL
import java.util.logging.Level
import java.util.logging.Logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.time.TimeSource


// AppEngine seems to ignore values of those timeouts
// and throws SocketTimeoutException after 50 seconds
private const val TIMEOUT = 30_000 //30s


@WebServlet(name = "CheckServlet", urlPatterns = ["/check"])
class CheckServlet : HttpServlet() {

    private val log = Logger.getLogger(CheckServlet::class.java.name)


    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {

        val timeSource = TimeSource.Monotonic
        val markStart = timeSource.markNow()

        val inputStream = try {
            val connection = URL(PidRssFeedParser.URL).openConnection()
            connection.connectTimeout = TIMEOUT
            connection.readTimeout = TIMEOUT
            connection.getInputStream()
        } catch (_: IOException) {
            val elapsed = timeSource.markNow() - markStart
            log.warning("Use secondary URL after ${elapsed.inWholeMilliseconds}ms") //TODO: show only as info
            val backupConnection = URL(PidRssFeedParser.BACKUP_URL).openConnection()
            backupConnection.connectTimeout = TIMEOUT
            backupConnection.readTimeout = TIMEOUT
            backupConnection.getInputStream()
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
