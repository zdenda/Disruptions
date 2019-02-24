package eu.zkkn.disruptions.backend

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener


/**
 * This ServletContextListener is setup in web.xml to run after the application starts up
 * and before it services the first request.
 */
class Bootstrapper : ServletContextListener {

    override fun contextInitialized(sce: ServletContextEvent?) {
        // ObjectifyService.init() no need for objectify 5,it's necessary for version 6
        if (sce != null) {
            ServletContextHolder.setServletContext(sce.servletContext)
        }
    }

    override fun contextDestroyed(sce: ServletContextEvent?) {
        // App Engine does not currently invoke this method.
    }

}
