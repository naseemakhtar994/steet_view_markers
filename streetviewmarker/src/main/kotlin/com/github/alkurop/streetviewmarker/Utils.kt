package com.github.alkurop.streetviewmarker

import com.google.android.gms.maps.model.LatLng


fun calculatePlaceDistance(myLocation: LatLng, markerLocation: LatLng): Double {
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
