package eu.zkkn.disruptions.backend

import java.net.URL
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@WebServlet(name = "RssProxyServlet", urlPatterns = ["/proxy"])
class RssProxyServlet : HttpServlet() {

    private val url = URL("https://pid.cz/feed/rss-mimoradnosti")

    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse) {
        resp.contentType = "text/xml; charset=UTF-8"
        url.openStream().copyTo(resp.outputStream)
    }

}
