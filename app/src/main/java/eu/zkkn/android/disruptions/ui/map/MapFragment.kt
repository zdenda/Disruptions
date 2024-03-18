package eu.zkkn.android.disruptions.ui.map

import android.graphics.Color
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import eu.zkkn.android.disruptions.R
import eu.zkkn.android.disruptions.data.Preferences
import eu.zkkn.android.disruptions.data.SubscriptionRepository
import eu.zkkn.android.disruptions.ui.AnalyticsFragment
import eu.zkkn.android.disruptions.utils.BitmapHelper
import eu.zkkn.android.disruptions.utils.ioThread
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.URL
import kotlin.math.absoluteValue

class MapFragment : AnalyticsFragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        //val sydney = LatLng(-34.0, 151.0)
        //googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney").rotation(90F))
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        val colors = mutableSetOf("#d50000", "#c51162", "#aa00ff", "#6200ea", "#304ffe", "#2962ff", "#0091ea", "#00b8d4", "#00bfa5", "#00c853", "#64dd17", "#aeea00", "#ffab00", "#ff6d00", "#dd2c00", "#3e2723", "#212121", "#263238").toMutableList()
        colors.shuffle()

        ioThread {

            val bounds = LatLngBounds.builder()

            val allLines = SubscriptionRepository.getInstance(requireContext()).getAllLineNames().take(10)

            for (line in allLines) {

                val url = URL("https://api.golemio.cz/v2/public/vehiclepositions?routeShortName=$line")
                val connection = url.openConnection()
                connection.setRequestProperty("x-access-token", "zkkn.apps+mimoradnosti@gmail.com") //FIXME
                val data = InputStreamReader(connection.getInputStream(), Charsets.UTF_8).readText()

                val icon: BitmapDescriptor by lazy {
                    //val color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                    val color = Color.parseColor(if (colors.size > 0) colors.removeFirst() else "#000000")
                    BitmapHelper.vectorToBitmap(
                        requireContext(),
                        R.drawable.ic_compass_arrow_2,
                        color
                    )
                }

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

                    val marker = MarkerOptions().title(shortName)
                        .position(position)
                        .rotation((bearing).toFloat())
                        .snippet(
                            "${if (delay > 0) "-" else "+"}${DateUtils.formatElapsedTime(delay.absoluteValue)} $vehicleId"
                        )
                        .icon(icon)
                    requireActivity().runOnUiThread {
                        googleMap.addMarker(marker)
                    }

                    bounds.include(position)
                }
            }

            requireActivity().runOnUiThread {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 20))
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
        view.findViewById<View>(R.id.mapLayout).visibility = View.VISIBLE
        view.findViewById<View>(R.id.mapDialog).visibility = View.GONE
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

}