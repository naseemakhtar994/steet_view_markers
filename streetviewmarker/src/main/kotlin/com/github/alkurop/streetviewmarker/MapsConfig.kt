package com.github.alkurop.streetviewmarker


/**
 * Created by alkurop on 24.05.16.
 */
data class MapsConfig(
    val xMapCameraAngle: Double = 90.toDouble(),
    val yMapCameraAngle: Double = 100.toDouble(),
    val markersToShowStreetRadius: Double = 500.toDouble(),
    val markerScaleRadius: Double = 50.toDouble(),
    val markerMinPositionToMoveToMarker: Double = 10.toDouble(),
    val minMarkerSize: Double = 0.2.toDouble(),
    val yOffset: Double = 0.05, // -1 offsets down, +1 offsets up
    val showIgnoringAzimuth: Boolean = false
)

