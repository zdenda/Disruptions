package eu.zkkn.disruptions.backend.data

import com.googlecode.objectify.Objectify
import com.googlecode.objectify.ObjectifyService


open class Dao {

    val objectify: Objectify = ObjectifyService.ofy()

    companion object {

        init {
            ObjectifyService.register(Disruption::class.java)
        }

    }

}
