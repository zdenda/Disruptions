package eu.zkkn.disruptions.backend

import com.google.appengine.api.utils.SystemProperty
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@WebServlet(name = "HelloAppEngine", urlPatterns = ["/hello"])
class HelloAppEngine : HttpServlet() {

    companion object {

        @JvmStatic
        fun getInfo(): String {
            return ("Version: " + System.getProperty("java.version")
                    + " OS: " + System.getProperty("os.name")
                    + " User: " + System.getProperty("user.name"))
        }

    }

    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val properties = System.getProperties()
        response.contentType = "text/plain"
        response.writer.println("Hello App Engine - Standard using "
                + SystemProperty.version.get() + " Java "
                + properties["java.specification.version"])
    }

}
