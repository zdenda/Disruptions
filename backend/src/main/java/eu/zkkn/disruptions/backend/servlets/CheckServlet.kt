package eu.zkkn.disruptions.backend.servlets

import com.google.gson.Gson
import eu.zkkn.disruptions.backend.data.Disruption
import eu.zkkn.disruptions.backend.data.DisruptionDao
import eu.zkkn.disruptions.backend.datasource.PidRssFeedParser
import java.net.URL
import java.util.*
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
        val pidRssFeed = PidRssFeedParser(url.openStream()).parse()
        log.info(pidRssFeed.toString())

        val disruptions = DisruptionDao()

        for (item in pidRssFeed.items) {
            val disruption = disruptions.load(item.guid)
            if (disruption != null) {
                disruption.lines = item.lines.toMutableSet()
                disruption.title = item.title
                disruption.timeInfo = item.timeInfo
                disruption.updated = Date()
                disruptions.save(disruption)
            } else {
                disruptions.save(Disruption.fromPidRssFeedItem(item))
                log.info("Send notifications to: ${item.lines}")
                //sendNotifications()
            }
        }

        resp.contentType = "application/json; charset=UTF-8"
        Gson().toJson(pidRssFeed, resp.writer)
    }

}
