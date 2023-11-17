package eu.zkkn.disruptions.backend

import com.google.appengine.api.utils.SystemProperty


object Utils {

    /**
     * To determine whether code is running in production or in the local development server
     * @return true if this runs on a production server; false otherwise
     */
    fun isProduction(): Boolean {
        return SystemProperty.Environment.Value.Production == SystemProperty.environment.value()
    }

}
