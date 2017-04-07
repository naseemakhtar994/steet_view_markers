package com.github.alkurop.streetviewmarker.components

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.SurfaceHolder
import com.github.alkurop.streetviewmarker.BufferMarkerDrawData
import com.github.alkurop.streetviewmarker.MapsConfig
import com.github.alkurop.streetviewmarker.MarkerDrawData
import com.github.alkurop.streetviewmarker.MarkerGeoData
import com.github.alkurop.streetviewmarker.MarkerMatrixData
import com.github.alkurop.streetviewmarker.Place
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import java.util.*
import java.util.concurrent.*
import com.squareup.picasso.Target as PicassoTarget

/**
 * Created by alkurop on 31.05.16.
 */

interface IDrawThread {
  fun updateCamera(location: LatLng, bearing: Float, tilt: Float, zoom: Float)
  fun setRunning(run: Boolean)
}

class DrawThread(private val surfaceHolder: SurfaceHolder,
                 val resources: Resources,
                 val markers: List<Place>,
                 val drawData: MutableList<MarkerDrawData?>,
                 val context: Context,
                 var mapsConfig: MapsConfig)
  : Thread(), IDrawThread {
  val TAG = DrawThread::class.java.simpleName
  private val matrixSet = hashSetOf<MarkerMatrixData>()
  private val bitmapMap = ConcurrentHashMap<String, Bitmap>()
  private val targetMap = ConcurrentHashMap<String, PicassoTarget>()
  private val locBufferMap = HashMap<String, LinkedList<BufferMarkerDrawData>>()
  private val picassoHandler: Handler = Handler(Looper.getMainLooper())

  private var initX: Double = 0.0
  private var initY: Double = 0.0
  private var runFlag = false
  private var calcFlag = false
  private var mLocation: LatLng? = null
  private var mBearing: Double = 0.0
  private var mTilt: Double = 0.0
  private var mZoom: Double = 0.0
  private var screenHeight: Double = 0.0
  private var screenWidth: Double = 0.0
  private var xTransitionDim: Double = 0.0
  private var yTransitionDim: Double = 0.0
  private var xCalcAngle: Double = 0.0
  private var yCalcAngle: Double = 0.0

  override fun setRunning(run: Boolean) {
    runFlag = run
  }

  override fun run() {
    var canvas: Canvas?
    while (runFlag) {
      if (calcFlag)
        calculate()

      canvas = null
      try {
        screenHeight = surfaceHolder.surfaceFrame.height().toDouble()
        screenWidth = surfaceHolder.surfaceFrame.width().toDouble()
        if (screenWidth > screenHeight) {
          screenWidth = surfaceHolder.surfaceFrame.height().toDouble()
          screenHeight = surfaceHolder.surfaceFrame.width().toDouble()
        }

        initX = (screenWidth.toDouble() * 0.3).toDouble()
        initY = initX
        canvas = surfaceHolder.lockCanvas(null)
        synchronized(surfaceHolder) {
          canvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
          if (canvas != null)
            drawMarkersOnCanvas(canvas!!)
        }
      } catch(e: Exception) {
        e.printStackTrace()
      } finally {
        surfaceHolder.unlockCanvasAndPost(canvas)
      }
    }
    bitmapMap.clear()
  }

  private fun drawMarkersOnCanvas(canvas: Canvas) {
    val drawDataList = matrixSet.map { matrixData ->
      var markerData: MarkerDrawData? = null
      val key = matrixData.data.id
      if (matrixData.shouldShow) {
        with(matrixData) {
          bitmapMap.containsKey(key = key)
          val bitmap = if (bitmapMap.containsKey(key)) {
            bitmapMap[key]!!
          } else {
            val bitmap = BitmapFactory.decodeResource(resources, data.place.drawable)
            bitmapMap.put(key, bitmap)
            loadPicture(key, data.place.markerPath)
            bitmap
          }

          val xLeft = (xLoc - (initX / 2.toDouble() * scale))
          val yTop = yLoc - initY / 2.toDouble() * scale
          val xRight = (xLoc + (initX / 2 * scale))
          val yBot = yLoc + initY / 2.toDouble() * scale

          val bufferData = BufferMarkerDrawData(xLeft, yTop, xRight, yBot)

          val latestBufferList = if (locBufferMap.contains(key)) locBufferMap[key]!! else {
            val mList = LinkedList<BufferMarkerDrawData>()
            for (x in 1..5) {
              mList.add(bufferData)
            }
            locBufferMap.put(key, mList)
            mList
          }
          val bufferDataCentered = BufferMarkerDrawData(xLeft, yTop, xRight, yBot)
          latestBufferList.forEach {
            bufferDataCentered.left += it.left
            bufferDataCentered.top += it.top
            bufferDataCentered.right += it.right
            bufferDataCentered.bottom += it.bottom
          }
          bufferDataCentered.left /= latestBufferList.size + 1
          bufferDataCentered.top /= latestBufferList.size + 1
          bufferDataCentered.right /= latestBufferList.size + 1
          bufferDataCentered.bottom /= latestBufferList.size + 1

          latestBufferList.removeLast()
          latestBufferList.addFirst(bufferData)

          val rec = RectF(
              bufferDataCentered.left.toFloat(),
              bufferDataCentered.top.toFloat(),
              bufferDataCentered.right.toFloat(),
              bufferDataCentered.bottom.toFloat())
          canvas.drawBitmap(bitmap, null, rec, null)
          markerData = MarkerDrawData(
              matrixData,
              bufferDataCentered.left.toFloat(),
              bufferDataCentered.top.toFloat(),
              bufferDataCentered.right.toFloat(),
              bufferDataCentered.bottom.toFloat())
        }
      }
      if (!matrixData.shouldShow) {
        locBufferMap.remove(key)
      }
      markerData
    }.filter { it != null }
    drawData.clear()
    drawData.addAll(drawDataList)
  }

  private fun loadPicture(key: String, path: String) {
    if (!TextUtils.isEmpty(path) && !targetMap.containsKey(key)) {
      Log.d(TAG, "bitmap load started")
      val target = object : PicassoTarget {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {
          targetMap.remove(key)
          Log.d(TAG, "bitmap load failed")
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
          if (bitmap != null) {
            bitmapMap.put(key, bitmap)
            Log.d(TAG, "bitmap load completed")
          } else {
            Log.d(TAG, "bitmap decode failed")
          }
          targetMap.remove(key)
        }
      }
      targetMap.put(key, target)
      picassoHandler.post { Picasso.with(context).load(path).config(Bitmap.Config.ARGB_4444).into(target) }
    }
  }

  override fun updateCamera(location: LatLng, bearing: Float, tilt: Float, zoom: Float) {
    if (!(mLocation?.equals(location) ?: false))
      locBufferMap.clear()
    mLocation = location
    mBearing = bearing.toDouble()
    mTilt = tilt.toDouble()
    mZoom = 1.0 + zoom.toDouble()
    xCalcAngle = mapsConfig.xMapCameraAngle.toDouble() / mZoom
    yCalcAngle = mapsConfig.yMapCameraAngle.toDouble() / mZoom
    calcFlag = true
    xTransitionDim = screenWidth.toDouble() / xCalcAngle
    yTransitionDim = screenHeight.toDouble() / yCalcAngle
  }

  private fun calculate() {
    if (mLocation !== null) {
      val geoData = markers.map { calculateGeoData(mLocation!!, it) }
      val data = geoData.map { generateMatrix(it) }
      matrixSet.removeAll(data)
      matrixSet.addAll(data)
    }
    calcFlag = false

  }

  private fun generateMatrix(geoData: MarkerGeoData): MarkerMatrixData {


    val isInRange = geoData.distance * 1000 <= mapsConfig.markersToShowStreetRadius
    var xLoc = 0.toDouble()
    var yLoc = 0.toDouble()
    var scale = 0.toDouble()


    var shouldShow = false
    if (mBearing - geoData.azimuth > 180 || mBearing - geoData.azimuth < -180) {
      shouldShow = Math.abs(geoData.azimuth - mBearing + 360) < xCalcAngle ||
          Math.abs(geoData.azimuth - mBearing - 360) < xCalcAngle

    } else {
      shouldShow = Math.abs(geoData.azimuth - mBearing) < xCalcAngle
    }
    if (mapsConfig.showIgnoringAzimuth) shouldShow = true
    if (!isInRange) shouldShow = false

    if (shouldShow) {
      //log(TAG, geoData.toString())
      scale = ((mapsConfig.markerScaleRadius - geoData.distance * 1000.toDouble())
          / mapsConfig.markerScaleRadius)

      if (scale > 1) {
        scale = 1.toDouble()
      }
      if (scale <= mapsConfig.minMarkerSize.toDouble() &&
          mapsConfig.markersToShowStreetRadius.toDouble() - geoData.distance.toDouble() * 1000.toDouble() > 0) {
        scale = mapsConfig.minMarkerSize.toDouble()

      } else if (scale <= mapsConfig.minMarkerSize.toDouble()) {
        scale = 0.toDouble()
      }
      scale *= (mZoom - 1) / 2 + 1
      val correctedProjectionBearing =
          if (geoData.azimuth - mBearing >= mapsConfig.xMapCameraAngle * 1.5) {
            mBearing + 360
          } else if (mBearing - geoData.azimuth >= mapsConfig.xMapCameraAngle * 1.5) {
            mBearing - 360

          } else mBearing

      xLoc = ((geoData.azimuth - correctedProjectionBearing) * xTransitionDim) +
          screenWidth / 2.toDouble()

      yLoc = (screenHeight / 2.toDouble() + (mTilt * yTransitionDim)) * (1.toDouble() - mapsConfig.yOffset)
    }

    val mat = MarkerMatrixData(
        geoData,
        shouldShow,
        scale,
        xLoc,
        yLoc)
    return mat
  }

  private fun calculateGeoData(location: LatLng, place: Place): MarkerGeoData {
    val distance = calculatePlaceDistance(location, LatLng(place.location.lat, place.location.lng))
    val azimuth = calculatePlaceAzimuth(location, LatLng(place.location.lat, place.location.lng))
    return MarkerGeoData(place, distance, azimuth)
  }

  private fun calculatePlaceAzimuth(myLocation: LatLng, markerLocation: LatLng): Double {
    val dLon = markerLocation.longitude - myLocation.longitude
    val y = Math.sin(dLon) * Math.cos(markerLocation.latitude)
    val x = Math.cos(myLocation.latitude) * Math.sin(markerLocation.latitude) -
        Math.sin(myLocation.latitude) * Math.cos(markerLocation.latitude) * Math
            .cos(dLon)
    var angle = Math.atan2(y, x) * 180 / Math.PI
    if (angle < 0) angle += 360
    if (angle > 360) angle -= 360
    return angle
  }

  private fun calculatePlaceDistance(myLocation: LatLng, markerLocation: LatLng): Double {
    val R = 6371 // km
    val dLat = (myLocation.latitude - markerLocation.latitude).toRad()
    val dLon = (myLocation.longitude - markerLocation.longitude).toRad()
    val lat1 = myLocation.latitude.toRad()
    val lat2 = markerLocation.latitude.toRad()
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    val d = R * c
    return d
  }
}


fun Double.toRad(): Double = this * Math.PI / 180
