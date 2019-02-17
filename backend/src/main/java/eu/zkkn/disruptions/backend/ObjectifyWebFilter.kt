package eu.zkkn.disruptions.backend

import com.googlecode.objectify.ObjectifyFilter
import javax.servlet.annotation.WebFilter


@WebFilter(urlPatterns = ["/*"])
class ObjectifyWebFilter : ObjectifyFilter()
