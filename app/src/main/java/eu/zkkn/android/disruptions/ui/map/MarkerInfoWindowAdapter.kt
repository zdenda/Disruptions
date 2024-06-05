package eu.zkkn.android.disruptions.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import eu.zkkn.android.disruptions.R
import kotlin.math.absoluteValue


class MarkerInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? {
        // Return null to indicate that the default window (white bubble) should be used
        return null
    }


    override fun getInfoContents(marker: Marker): View? {
        val data = marker.tag as? MarkerData ?: return null

        @SuppressLint("InflateParams") // it was in Google Maps Tutorial
        val view = LayoutInflater.from(context).inflate(R.layout.map_marker_info, null).apply {
            findViewById<TextView>(R.id.tvTitle).text = data.lineName
            findViewById<TextView>(R.id.tvVehicleId).text = "Číslo: ${data.vehicleId}"
            findViewById<TextView>(R.id.tvDelay).text =
                "${if (data.delay > 0) "Zpoždění" else "Zrychlení"}: ${DateUtils.formatElapsedTime(data.delay.absoluteValue)}"
        }
        return view
    }

}
