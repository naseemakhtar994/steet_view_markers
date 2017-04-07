package com.github.alkurop.mylibrary

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.widget.LinearLayout
import com.github.alkurop.mylibrary.components.IStreetOverlayView
import com.github.alkurop.mylibrary.components.StreetOverlayView
import com.github.alkurop.mylibrary.components.TouchOverlayView
import com.google.android.gms.maps.StreetViewPanoramaView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.StreetViewPanoramaCamera
import java.util.*

/**
 * Created by alkurop on 2/3/17.
 */
class StreetMarkerView : LinearLayout, IStreetOverlayView {
  val overlay: StreetOverlayView

  val streetView: StreetViewPanoramaView
  val touchOverlay: TouchOverlayView
  var onSteetLoadedSuccess: ((Boolean) -> Unit)? = null
  var onCameraUpdateListener: ((UpdatePosition) -> Unit)? = null
  override var mapsConfig: MapsConfig
    set(value) {
      overlay.mapsConfig = value
    }
    get() = overlay.mapsConfig

  var shouldFocusToMyLocation = true
  var markerDataList = hashSetOf<Place>()

  @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0)
      : super(context, attrs, defStyleAttr, defStyleRes) {
    inflate(context, R.layout.view_street_marker, this)
    overlay = findViewById(R.id.panorama) as StreetOverlayView
    streetView = findViewById(R.id.panorama) as StreetViewPanoramaView
    touchOverlay = findViewById(R.id.touch) as TouchOverlayView


  }

  override fun onLocationUpdate(location: LatLng) {
    overlay.onLocationUpdate(location)
  }

  override fun onCameraUpdate(cameraPosition: StreetViewPanoramaCamera) {
    overlay.onCameraUpdate(cameraPosition)
  }

  override fun addMarkers(markers: HashSet<Place>) {
    val addMarkers = markers.filter { marker ->
      !markerDataList.contains(marker)
    }
    if (addMarkers.isNotEmpty())
      overlay.addMarkers(markers)
    markerDataList.addAll(addMarkers)
  }

  override fun onClick() {
    overlay.onClick()
  }

  override fun setOnMarkerClickListener(onClickListener: ((data: MarkerDrawData) -> Unit)?) {
    overlay.setOnMarkerClickListener(onClickListener)
  }

  private fun sendCameraPosition(position: LatLng) {
    val updatePosition = UpdatePosition(Location(position.latitude,
        position.longitude), 500)
    onCameraUpdateListener?.invoke(updatePosition)
  }

  fun onMarkerClicker(geoData: MarkerGeoData) {
    if (geoData.distance >= mapsConfig.markerMinPositionToMoveToMarker / 1000.toDouble()) {
      focusToLocation(geoData.place.location)
    }
  }

  //CONTROLS

  fun focusToLocation(location: Location) {
    streetView.getStreetViewPanoramaAsync { panorama ->
      panorama.setPosition(LatLng(location.lat, location.lng))
      sendCameraPosition(LatLng(location.lat, location.lng))
    }
  }


  //State callbacks

  fun onCreate(state: Bundle?) {
    streetView.onCreate(state)
    streetView.getStreetViewPanoramaAsync { panorama ->
      panorama.setOnStreetViewPanoramaCameraChangeListener { cameraPosition ->
        overlay.onCameraUpdate(cameraPosition)
      }
      panorama.setOnStreetViewPanoramaChangeListener { cameraPosition ->
        if (cameraPosition != null && cameraPosition.position != null) {
          overlay.onLocationUpdate(cameraPosition.position)
          sendCameraPosition(cameraPosition.position)
        }
        onSteetLoadedSuccess?.invoke(cameraPosition != null && cameraPosition.links != null)
      }
    }
    touchOverlay.onTouchListener = {
      overlay.onTouchEvent(it)
    }
    overlay.setOnMarkerClickListener {
      onMarkerClicker(it.matrixData.data)
    }
    restoreState(state)
  }

  private fun restoreState(saveState: Bundle?) {
    saveState?.let {
      shouldFocusToMyLocation = saveState.getBoolean("shouldFocusToMyLocation", true)
      markerDataList = (saveState.getParcelableArray("markerModels") as Array<Place>).toHashSet()
    }
  }

  fun onSaveInstanceState(state: Bundle?): Bundle {
    val bundle = state ?: Bundle()
    bundle.putParcelableArray("markerModels", markerDataList.toTypedArray())
    bundle.putBoolean("shouldFocusToMyLocation", shouldFocusToMyLocation)
    streetView.onSaveInstanceState(bundle)
    return bundle
  }

  fun onResume() {
    streetView.onResume()
  }

  fun onPause() {
    streetView.onPause()
  }

  fun onDestroy() {
    streetView.onDestroy()
  }

  fun onLowMemory() = streetView.onLowMemory()
}