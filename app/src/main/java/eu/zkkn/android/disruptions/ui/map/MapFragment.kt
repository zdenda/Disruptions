package eu.zkkn.android.disruptions.ui.map

import android.graphics.Color
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import eu.zkkn.android.disruptions.R
import eu.zkkn.android.disruptions.data.SubscriptionRepository
import eu.zkkn.android.disruptions.utils.BitmapHelper
import eu.zkkn.android.disruptions.utils.ioThread
import org.json.JSONObject
import java.net.URL
import kotlin.math.absoluteValue


class MapFragment : Fragment() {

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

            val allLines = SubscriptionRepository.getInstance(requireContext()).getAllLineNames()

            for (line in allLines) {

                val data =
                    URL("https://api.golemio.cz/v2/public/vehiclepositions?routeShortName=$line").readText()

                //println("ZKLog 0: $data")

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
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}