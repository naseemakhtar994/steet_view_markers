package com.github.alkurop.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_demo.*


class DemoActivity :AppCompatActivity(){
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_demo)
    marker_view.onCreate(savedInstanceState)
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
}