package eu.zkkn.disruptions.backend

import com.google.appengine.api.utils.SystemProperty
import java.net.HttpURLConnection
import java.net.URL

// AppEngine seems to ignore values of those timeouts
// and throws SocketTimeoutException after 50 seconds
private const val TIMEOUT = 30_000 //30s


object Utils {

    /**
     * To determine whether code is running in production or in the local development server
     * @return true if this runs on a production server; false otherwise
     */
    fun isProduction(): Boolean {
        return SystemProperty.Environment.Value.Production == SystemProperty.environment.value()
    }

    fun openHttpConnection(url: String): HttpURLConnection {
        val connection = URL(url).openConnection()
        connection.connectTimeout = TIMEOUT
        connection.readTimeout = TIMEOUT

        if (connection !is HttpURLConnection) throw RuntimeException("Unexpected Connection Type")

        return connection
    }

}
