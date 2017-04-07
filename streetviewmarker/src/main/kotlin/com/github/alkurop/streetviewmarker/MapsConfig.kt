package com.github.alkurop.streetviewmarker


/**
 * Created by alkurop on 24.05.16.
 */
data class MapsConfig(
    /**
     * Camera angle for marker view. The more angle the more markers will
     * be visible horizontally.
     * Adapt it if looks like markers are not synced with cam horizontally
     */
    val xMapCameraAngle: Double = 90.toDouble(),

    /**
     * Camera angle for marker view. The more angle the more markers will
     * be visible vertically.
     * Adapt it if looks like markers are not synced with cam vertically
     */
    val yMapCameraAngle: Double = 100.toDouble(),


    /**
     * only markers that are 500 meters away will be shown in street view.
     */
    val markersToShowStreetRadius: Double = 500.toDouble(),


    /**
     * markers will scale down unitl they are as far as 50 meters.
     */
    val markerScaleRadius: Double = 50.toDouble(),


    /**
     *  if marker is closer the 10 meters user will move the camera to the marker.
     *  Otherwise click on marker will be executed
     */
    val markerMinPositionToMoveToMarker: Double = 10.toDouble(),


    /**
     * marker will not scale down when moving away less then 20%
     */
    val minMarkerSize: Double = 0.2,


    /**
     * marker will be shown a bit above the ground. 0 will be straight ground.
     */
    val yOffset: Double = 0.05,


    /**
     * this is here for debugging. don't touch it.
     */
    val showIgnoringAzimuth: Boolean = false
)

