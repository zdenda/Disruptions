package eu.zkkn.android.disruptions.ui.map

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import eu.zkkn.android.disruptions.R
import eu.zkkn.android.disruptions.data.Preferences
import eu.zkkn.android.disruptions.data.SubscriptionRepository
import eu.zkkn.android.disruptions.ui.AnalyticsFragment
import eu.zkkn.android.disruptions.utils.Analytics
import eu.zkkn.android.disruptions.utils.BitmapHelper
import eu.zkkn.android.disruptions.utils.RemoteConfigKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class MapFragment : AnalyticsFragment() {

    private var isAnalyticsEventLogged = false

    private var markers = mapOf<String, Marker>()

    private val lines: List<LineData> by lazy {
        // uses green, yellow and red for metro lines
        val colors = mutableSetOf("#d50000", "#c51162", "#aa00ff", "#6200ea", "#304ffe", "#2962ff", "#0091ea", "#00b8d4", "#00bfa5", "#00c853", "#64dd17", "#aeea00", "#ffab00", "#ff6d00", "#dd2c00", "#3e2723", "#212121", "#263238").toMutableList()
        colors.shuffle()

        SubscriptionRepository.getInstance(requireContext()).getAllLineNames()
            .take(10) // show info if user has more subscribed lines
            .map { lineName ->
                val icon: BitmapDescriptor by lazy {
                    //val color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                    val color = Color.parseColor(if (colors.size > 0) colors.removeAt(0) else "#000000")
                    BitmapHelper.vectorToBitmap(
                        requireContext(),
                        R.drawable.ic_compass_arrow_2,
                        color
                    )
                }
                LineData(lineName, icon)
            }
    }

    private var myGoogleMap: MyGoogleMap? = null

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        myGoogleMap = MyGoogleMap(googleMap)

        lifecycleScope.launch {

            myGoogleMap?.let {
                val prague = LatLng(50.0875, 14.421389)
                it.moveCamera(CameraUpdateFactory.newLatLngZoom(prague, 10f))
                it.setInfoWindowAdapter(MarkerInfoWindowAdapter(requireContext()))
                updatePositions(it)
            }
        }

    }

    private suspend fun updatePositions(googleMap: MyGoogleMap) {

        val golemioApiKey = Firebase.remoteConfig.getString(RemoteConfigKeys.GOLEMIO_API_KEY)
        if (golemioApiKey.isEmpty()) return

        withContext(Dispatchers.IO) {

            val newMarkers = mutableMapOf<String, Marker>()
            val bounds = LatLngBounds.builder()
            var hasPositions = false

            for (line in lines) {
                ensureActive() // check if it works

                val url = URL("https://api.golemio.cz/v2/public/vehiclepositions?routeShortName=${line.lineName}")
                val connection = url.openConnection() as HttpsURLConnection
                connection.setRequestProperty("x-access-token", golemioApiKey)
                connection.connect()
                if (connection.responseCode !in 200..299) continue

                val data = InputStreamReader(connection.inputStream, Charsets.UTF_8).readText()

                val features = JSONObject(data).getJSONArray("features")
                for (i in 0 until features.length()) {
                    val current = features.getJSONObject(i)
                    val coordinates = current.getJSONObject("geometry").getJSONArray("coordinates")
                    //println("ZKLog Coordinates: ${coordinates.getDouble(0)}, ${coordinates.getDouble(1)}")
                    val properties = current.getJSONObject("properties")

                    val shortName = properties.getString("gtfs_route_short_name")
                    val vehicleId = properties.getString("vehicle_id")
                    val bearing = properties.optInt("bearing", 0)
                    val delay = properties.optLong("delay", 0)
                    val position = LatLng(coordinates.getDouble(1), coordinates.getDouble(0))

                    val markerData = MarkerData(shortName, vehicleId, delay)

                    val marker = markers[vehicleId]

                    if (marker == null) {
                        val markerOptions = MarkerOptions().title(shortName)
                            .position(position)
                            .rotation((bearing).toFloat())
                            .icon(line.icon)
                        val newMarker = withContext(Dispatchers.Main) {
                            googleMap.addMarker(markerOptions).apply {
                                if (this != null) tag = markerData
                            }
                        }
                        if (newMarker != null) newMarkers[vehicleId] = newMarker
                    } else {
                        requireActivity().runOnUiThread {
                            marker.position = position
                            marker.rotation = bearing.toFloat()
                            if (markerData != marker.tag) {
                                marker.tag = markerData
                                // update data in info window and move camera if tooltip is shown
                                if (marker.isInfoWindowShown) {
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(position))
                                    marker.showInfoWindow()
                                }
                            }
                        }
                        newMarkers[vehicleId] = marker
                    }

                    bounds.include(position)
                    hasPositions = true
                }
            }

            if (newMarkers.isNotEmpty()) {
                markers.minus(newMarkers.keys).forEach { (_, marker) ->
                    requireActivity().runOnUiThread { marker.remove() }
                }
                markers = newMarkers
            }

            if (hasPositions && !googleMap.hasUserMovedCamera) {
                val cameraPosition = CameraUpdateFactory.newLatLngBounds(bounds.build(), 48)
                requireActivity().runOnUiThread {
                    googleMap.moveCamera(cameraPosition)
                }
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Preferences.isRealtimePositionsEnabled(requireContext()) == true) {
            showMap(view)
        }

        view.findViewById<Button>(R.id.buttonPositive).setOnClickListener {
            if (!isAnalyticsEventLogged) {
                isAnalyticsEventLogged = true
                Analytics.logRealtimeMapEnabled(true)
            }
            Preferences.setRealtimePositionsEnabled(requireContext(), true)
            AlertDialog.Builder(requireContext()).apply {
                setTitle("Poloha vozů na mapě")
                setMessage("Zobrazení aktuální polohy vozů sledovaných linek na mapě je prozatím jen experiment ve velmi ranném stádiu vývoje. Proto nejspíše nebude fungovat spolehlivě a v budoucnu může z aplikace zcela zmizet. Nyní slouží především k prověření funkčnosti a získání zpětné vazby.")
                setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                show()
            }
            showMap(view)
        }

        view.findViewById<Button>(R.id.buttonNegative).setOnClickListener { button ->
            if (!isAnalyticsEventLogged) {
                isAnalyticsEventLogged = true
                Analytics.logRealtimeMapEnabled(false)
            }
            Preferences.setRealtimePositionsEnabled(requireContext(), false)
            button.isEnabled = false
            Snackbar
                .make(requireContext(), view, "Uloženo. Záložka Mapa zmizí po restartu aplikace.", Snackbar.LENGTH_LONG)
                .setAction("Zpět") {
                    Preferences.resetRealtimePositionsEnabled(requireContext())
                    button.isEnabled = true
                }
                .show()
        }
    }

    private fun showMap(view: View) {

        if (Firebase.remoteConfig.getString(RemoteConfigKeys.GOLEMIO_API_KEY).isEmpty()) {
            Snackbar.make(view, "Aktuální polohu vozů nyní nelze načíst", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK") {}
                .show()
        }

        view.findViewById<View>(R.id.mapLayout).visibility = View.VISIBLE
        view.findViewById<View>(R.id.mapDialog).visibility = View.GONE
        view.findViewById<View>(R.id.fabRefresh).setOnClickListener {
            lifecycleScope.launch {
                myGoogleMap?.let { updatePositions(it) }
            }
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        // Move position of the map zoom controls, so they wouldn't be under the refresh button
        fixZoomControlsPosition(mapFragment)

    }

    @SuppressLint("ResourceType")
    private fun fixZoomControlsPosition(mapFragment: SupportMapFragment?) {
        // It's a Hack and it will break, but couldn't find a better solution
        mapFragment?.view?.findViewById<View>(1)?.setPadding(0, 0, 36, 200)
    }
}
