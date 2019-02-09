package eu.zkkn.disruptions.backend

import com.google.gson.Gson
import java.net.URL
import java.util.logging.Logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@WebServlet(name = "CheckServlet", urlPatterns = ["/check"])
class CheckServlet : HttpServlet() {

    private val log = Logger.getLogger(CheckServlet::class.java.name)
    private val url = URL("https://pid.cz/feed/rss-mimoradnosti")

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val pidRssFeed = PidRssFeedParser(url.openStream()).parse()
        log.info(pidRssFeed.toString())
        resp.contentType = "application/json; charset=UTF-8"
        Gson().toJson(pidRssFeed, resp.writer)
    }

}
