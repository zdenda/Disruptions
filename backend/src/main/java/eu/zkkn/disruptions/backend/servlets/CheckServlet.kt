package eu.zkkn.disruptions.backend.servlets

import eu.zkkn.disruptions.backend.MyGson
import eu.zkkn.disruptions.backend.data.Disruption
import eu.zkkn.disruptions.backend.data.DisruptionDao
import eu.zkkn.disruptions.backend.datasource.PidRssFeedParser
import eu.zkkn.disruptions.backend.messaging.Messaging
import java.net.URL
import java.util.logging.Logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@WebServlet(name = "CheckServlet", urlPatterns = ["/check"])
class CheckServlet : HttpServlet() {

    private val log = Logger.getLogger(CheckServlet::class.java.name)


    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val url = URL(PidRssFeedParser.URL)
        val connection = url.openConnection()
        connection.connectTimeout = 60_000 // 60s
        connection.readTimeout = 60_000 // 60s
        val pidRssFeed = PidRssFeedParser(connection.getInputStream()).parse()
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
    }

}
