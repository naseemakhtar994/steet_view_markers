package com.github.alkurop.mylibrary

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
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
class StreetMarkerView : LinearLayout , IStreetOverlayView{


  val overlay: StreetOverlayView
  val streetView: StreetViewPanoramaView
  val touchOverlay: TouchOverlayView
  var onSteetLoadedSuccess : ((Boolean) -> Unit)? = null

  @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0)
      : super(context, attrs, defStyleAttr, defStyleRes) {
    inflate(context, R.layout.view_street_marker, this)
    overlay = findViewById(R.id.panorama) as StreetOverlayView
    streetView = findViewById(R.id.panorama) as StreetViewPanoramaView
    touchOverlay = findViewById(R.id.touch) as TouchOverlayView
    touchOverlay.onTouchListener = {
      overlay.onTouchEvent(it)
    }
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
  }

  override fun onLocationUpdate(location: LatLng) {
    overlay.onLocationUpdate(location)
  }

  override fun onCameraUpdate(cameraPosition: StreetViewPanoramaCamera) {
    overlay.onCameraUpdate(cameraPosition)
  }

  override fun addMarkers(markers: HashSet<Place>) {
    overlay.addMarkers(markers)
  }

  override fun onClick() {
    overlay.onClick()
  }

  override fun setOnMarkerClickListener(onClickListener: ((data: MarkerDrawData) -> Unit)?) {
    overlay.setOnMarkerClickListener (onClickListener)
  }

  private fun sendCameraPosition(position: LatLng?) {

  }
}