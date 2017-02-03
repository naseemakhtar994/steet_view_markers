package com.github.alkurop.mylibrary


/**
 * Created by alkurop on 24.05.16.
 */
object MapsConfig {
    val mapZoom = 12f
    val maxMarkersPerIterationCount = 20
    val markersOverflowCount = 50
    val updateInterval = 1000L
    val fastestUpdateInterval = 100L
    val markersToShowRadiusXScreen = 1.1.toDouble()
    val isBuildingsEnabled = true
    val isIndoorEnabled = true
    val isTrafficEnabled = false
    val isMyLocationEnabled = true
    val xMapCameraAngle = 90.toDouble()
    val yMapCameraAngle = 100.toDouble()
    val markersToShowStreetRadius: Double = 500.toDouble()
    val markerScaleRadius: Double = 50.toDouble()
    val markerMinPositionToMoveToMarker: Double = 10.toDouble()
    val minMarkerSize = 0.2.toDouble()
    val yOffset = 0.05 // -1 offsets down, +1 offsets up
    val showIgnoringAzimuth = false
}

