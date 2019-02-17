package eu.zkkn.disruptions.backend

import com.googlecode.objectify.ObjectifyService
import eu.zkkn.disruptions.backend.data.Disruption
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener


/**
 * This ServletContextListener is setup in web.xml to run after the application starts up
 * and before it services the first request.
 */
class Bootstrapper : ServletContextListener {

    override fun contextInitialized(sce: ServletContextEvent?) {

        //ObjectifyService.init()
        //ObjectifyService.register(Disruption::class.java)

    }

    override fun contextDestroyed(sce: ServletContextEvent?) {
        // App Engine does not currently invoke this method.
    }

}
