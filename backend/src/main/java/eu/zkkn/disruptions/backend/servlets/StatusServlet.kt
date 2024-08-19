package eu.zkkn.disruptions.backend.servlets

import com.google.gson.Gson
import com.google.gson.JsonObject
import eu.zkkn.disruptions.backend.Utils
import eu.zkkn.disruptions.backend.datasource.PidRssFeedParser
import java.io.IOException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@WebServlet(name = "StatusServlet", urlPatterns = ["/status"])
class StatusServlet: HttpServlet() {

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        var isSuccess = false

        try {
            val httpConnection = Utils.openHttpConnection(PidRssFeedParser.URL)
            if (httpConnection.responseCode in 200..299) {
                isSuccess = true
            }
        } catch (_: IOException) {
            isSuccess = false
        }

        resp.contentType = "application/json; charset=UTF-8"
        Gson().toJson(JsonObject().apply {
            addProperty("isSuccess", isSuccess)
        }, resp.writer)
    }

}
