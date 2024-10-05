package eu.zkkn.android.disruptions.ui.map

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

// Only interfaces can be delegated to
// class MyGoogleMap(googleMap: GoogleMap): GoogleMap by googleMap

class MyGoogleMap(private val googleMap: GoogleMap) {

    private var isCameraMoveListenerActive = true

    var hasUserMovedCamera = false

    init {
        googleMap.setOnCameraMoveStartedListener { //reason ->
            // println("Camera moved $reason, $isCameraMoveListenerActive, $hasUserMovedCamera")

            // same reason (REASON_DEVELOPER_ANIMATION) is used both when
            //   - zoom controls are used by user (considered as user moving the camera)
            //   - method GoogleMap.moveCamera() is called (NOT considered as user moving the camera)
            // so I couldn't find any other way to distinguish them than this
            if (isCameraMoveListenerActive) hasUserMovedCamera = true
        }
    }

    fun addMarker(markerOptions: MarkerOptions): Marker? {
        return googleMap.addMarker(markerOptions)
    }

    fun moveCamera(cameraUpdate: CameraUpdate) {
        isCameraMoveListenerActive = false
        googleMap.moveCamera(cameraUpdate)
        isCameraMoveListenerActive = true
    }

    fun setInfoWindowAdapter(markerInfoWindowAdapter: MarkerInfoWindowAdapter) {
        return googleMap.setInfoWindowAdapter(markerInfoWindowAdapter)
    }
}
