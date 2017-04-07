package com.github.alkurop.streetviewmarker.components

import android.content.Context
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.github.alkurop.streetviewmarker.MapsConfig
import com.github.alkurop.streetviewmarker.MarkerDrawData
import com.github.alkurop.streetviewmarker.Place
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.StreetViewPanoramaCamera
import java.util.*
import java.util.concurrent.*

/**
 * Created by alkurop on 31.05.16.
 */
interface IStreetOverlayView {
  fun onLocationUpdate(location: LatLng)
  fun onCameraUpdate(cameraPosition: StreetViewPanoramaCamera)
  fun addMarkers(markers: HashSet<Place>)
  fun onClick()
  fun setOnMarkerClickListener(onClickListener: ((data: MarkerDrawData) -> Unit)?)
  fun onTouchEvent(event: MotionEvent?): Boolean
  var mapsConfig: MapsConfig

}

class StreetOverlayView : SurfaceView, IStreetOverlayView,
    SurfaceHolder.Callback {
  override var mapsConfig: MapsConfig  = MapsConfig()
  val TAG = StreetOverlayView::class.java.simpleName
  private val mMarkers = CopyOnWriteArrayList<Place>()
  private val mDrawMarkers = CopyOnWriteArrayList<MarkerDrawData?>()
  private var mCameraPosition: StreetViewPanoramaCamera? = null
  private var mLocation: LatLng? = null
  private var mDrawThread: DrawThread? = null
  private var mLatestTouchPoint: TouchPoint? = null
  private var listener: ((data: MarkerDrawData) -> Unit)? = null

  constructor   (context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

  constructor  (context: Context, attrs: AttributeSet) : super(context, attrs)

  constructor  (context: Context) : super(context)

  init {
    holder.addCallback(this)
    holder.setFormat(PixelFormat.TRANSPARENT)
  }

  override fun onLocationUpdate(location: LatLng) {
    this.mLocation = location
    val pos = mCameraPosition
    if (pos !== null) {
      mDrawThread?.updateCamera(location, pos.bearing, pos.tilt, pos.zoom)
    } else {
      mDrawThread?.updateCamera(location, 0.toFloat(), 0.toFloat(), 0.toFloat())
    }
  }

  override fun onCameraUpdate(cameraPosition: StreetViewPanoramaCamera) {
    this.mCameraPosition = cameraPosition
    if (mLocation !== null) {
      mDrawThread?.updateCamera(mLocation!!, cameraPosition.bearing, cameraPosition.tilt, cameraPosition.zoom)
    }
  }

  override fun addMarkers(markers: HashSet<Place>) {
    val filteredAdd = this.mMarkers.toHashSet()
    filteredAdd.addAll(markers)
    this.mMarkers.clear()
    this.mMarkers.addAll(filteredAdd)
  }


  override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    Log.d(TAG, "surface surfaceChanged")
  }

  override fun surfaceDestroyed(holder: SurfaceHolder?) {
    var retry = true;
    mDrawThread?.setRunning(false)
    while (retry) {
      try {
        mDrawThread?.join()
        retry = false

      } catch (e: InterruptedException) {
        e.printStackTrace()
      }
    }
  }

  override fun surfaceCreated(holder: SurfaceHolder?) {
    mDrawThread = DrawThread(getHolder(), resources, mMarkers, mDrawMarkers, context, mapsConfig)
    mDrawThread?.setRunning(true)
    mDrawThread?.start()
  }

  override fun onTouchEvent(event: MotionEvent?): Boolean {
    mLatestTouchPoint
    val x = event?.x
    val y = event?.y
    mLatestTouchPoint = TouchPoint(x!!, y!!)
    return false
  }

  override fun onClick() {
    if (mLatestTouchPoint != null) {

      Log.d(TAG, mLatestTouchPoint.toString())
      mDrawMarkers.sortedBy { it!!.matrixData.data.distance }.forEach {
        itt ->
        Log.d(TAG, itt!!.toString())
        if (mLatestTouchPoint!!.isInRange(itt)) {
          listener?.invoke(itt)
          return
        }
      }
    }
  }

  data class TouchPoint(val x: Float, val y: Float) {
    fun isInRange(data: MarkerDrawData?): Boolean {
      if (data == null)
        return false
      val inRangeX = x >= data.left && x <= data.right
      val inRangeY = y >= data.top && y <= data.bottom
      return inRangeX && inRangeY
    }

    override fun toString(): String {
      return "TouchPoint" + "x" + x + "y" + y
    }
  }

  override fun setOnMarkerClickListener(onClickListener: ((MarkerDrawData) -> Unit)?) {
    this.listener = onClickListener
  }

}
