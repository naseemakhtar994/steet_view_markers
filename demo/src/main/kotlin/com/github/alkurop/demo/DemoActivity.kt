package com.github.alkurop.demo

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.github.alkurop.streetviewmarker.Location
import com.github.alkurop.streetviewmarker.MapsConfig
import com.github.alkurop.streetviewmarker.Place
import kotlinx.android.synthetic.main.activity_demo.*


class DemoActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_demo)
    marker_view.onCreate(savedInstanceState)
    marker_view.focusToLocation(Location(50.447604999999996, 30.5221409999999998))
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    val markerLoc = Location(50.447604999999996, 30.5221409999999998)
    val place = MyPlace("test", markerLoc, "http://www.petakids.com/wp-content/uploads/2015/11/Cute-Red-Bunny.jpg", R.drawable.ic_launcher)
    marker_view.addMarkers(hashSetOf(place))
    setListeners()

    //this is how you can edit marker view. Change the config
    marker_view.mapsConfig = MapsConfig()
  }

  private fun setListeners() {
    marker_view.onMarkerClickListener = {
      Toast.makeText(this, "maker was clicked $it", Toast.LENGTH_SHORT).show()
    }
    marker_view.onStreetLoadedSuccess = { loadedSuccss ->
      if (!loadedSuccss) {
        Toast.makeText(this, "This place cannot be shown in street view. Show user some other view", Toast.LENGTH_SHORT).show()
      }
    }
    marker_view.onCameraUpdateListener = {
      Log.d("street_view", "camera position changed. new position $it")
    }
  }


  override fun onResume() {
    super.onResume()
    marker_view.onResume()
  }

  override fun onPause() {
    super.onPause()
    marker_view.onPause()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    marker_view.onLowMemory()
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    val markerState = marker_view.onSaveInstanceState(outState)
    super.onSaveInstanceState(markerState)
  }

  override fun onDestroy() {
    super.onDestroy()
    marker_view.onDestroy()
  }
}

data class MyPlace(val markerId: String, val markerLocation: Location, val iconPath: String, val drawableRes: Int) : Place {
  override fun getId(): String = markerId

  override fun getLocation(): Location = markerLocation

  override fun getMarkerPath(): String = iconPath

  override fun getDrawable(): Int = drawableRes

  companion object {
    @JvmField val CREATOR: Parcelable.Creator<MyPlace> = object : Parcelable.Creator<MyPlace> {
      override fun createFromParcel(source: Parcel): MyPlace = MyPlace(source)
      override fun newArray(size: Int): Array<MyPlace?> = arrayOfNulls(size)
    }
  }

  constructor(source: Parcel) : this(source.readString(), source.readParcelable<Location>(Location::class.java.classLoader), source.readString(), source.readInt())

  override fun describeContents() = 0

  override fun writeToParcel(dest: Parcel?, flags: Int) {
    dest?.writeString(markerId)
    dest?.writeParcelable(markerLocation, 0)
    dest?.writeString(iconPath)
    dest?.writeInt(drawableRes)
  }
}