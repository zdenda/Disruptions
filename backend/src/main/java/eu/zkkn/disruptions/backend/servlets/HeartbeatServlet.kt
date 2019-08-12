package eu.zkkn.disruptions.backend.servlets

import com.google.gson.Gson
import com.google.gson.JsonObject
import eu.zkkn.disruptions.backend.messaging.Messaging
import java.util.logging.Logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@WebServlet(name = "HeartbeatServlet", urlPatterns = ["/heartbeat"])
class HeartbeatServlet : HttpServlet() {

    private val log = Logger.getLogger(HeartbeatServlet::class.java.name)


    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val messageId = Messaging.send(Messaging.prepareHeartbeatMessage())
        log.info("Heartbeat (Message ID: $messageId)")

        val jsonObject = JsonObject().apply {
            addProperty("messageId", messageId)
        }

        resp.contentType = "application/json; charset=UTF-8"
        Gson().toJson(jsonObject, resp.writer)
    }

}
