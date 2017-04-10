package com.example.javademo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.github.alkurop.streetviewmarker.MapsConfig;
import com.github.alkurop.streetviewmarker.Place;
import com.github.alkurop.streetviewmarker.StreetMarkerView;
import com.github.alkurop.streetviewmarker.UpdatePosition;
import com.github.alkurop.streetviewmarker.UtilsKt;
import com.google.android.gms.maps.model.LatLng;
import java.util.HashSet;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

  StreetMarkerView marker_view;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    marker_view = (StreetMarkerView) findViewById(R.id.marker_view);
    marker_view.onCreate(savedInstanceState);
    marker_view.focusToLocation(new LatLng(50.447604999999996, 30.5221409999999998));

  }

  @Override
  protected void onPostCreate(@Nullable Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    LatLng markerLoc = new LatLng(50.447604999999996, 30.5221409999999998);
    Place marker = new MyPlace(
        "test",
        markerLoc,
        "http://www.petakids.com/wp-content/uploads/2015/11/Cute-Red-Bunny.jpg",
        R.mipmap.ic_launcher);
    HashSet<Place> markers = new HashSet<>();
    markers.add(marker);
    marker_view.addMarkers(markers);
    setListeners();
  }

  private void setListeners() {
    marker_view.setOnMarkerClickListener(new Function1<Place, Unit>() {
      @Override
      public Unit invoke(Place place) {
        Toast.makeText(MainActivity.this, "maker was clicked" + place.toString(), Toast.LENGTH_SHORT).show();
        return null;
      }
    });
    marker_view.setOnStreetLoadedSuccess(new Function1<Boolean, Unit>() {
      @Override
      public Unit invoke(Boolean loadedSuccss) {
        if (!loadedSuccss) {
          Toast.makeText(MainActivity.this, "This place cannot be shown in street view. Show user some other view", Toast.LENGTH_SHORT).show();
        }
        return null;
      }
    });
    marker_view.setOnCameraUpdateListener(new Function1<UpdatePosition, Unit>() {
      @Override
      public Unit invoke(UpdatePosition updatePosition) {
        Log.d("street_view", "camera position changed. new position" + updatePosition.toString());

       /*

       Iterate through all your markers and find out distance from camera to marker.

       Double distance = UtilsKt.calculatePlaceDistance(markerLocation, updatePosition.center);

       Then if distance >  marker_view.getMapsConfig().markersToShowStreetRadius ,
       add that marker. By default it is 500 meters.

       */

        return null;
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    marker_view.onResume();
  }

  @Override
  protected void onPause() {
    marker_view.onPause();
    super.onPause();
  }

  @Override
  public void onLowMemory() {
    marker_view.onLowMemory();
    super.onLowMemory();
  }

  @Override
  public void onSaveInstanceState(Bundle outState ) {
    Bundle state = marker_view.onSaveInstanceState(outState);
    super.onSaveInstanceState(state);
  }

  @Override
  protected void onDestroy() {
    marker_view.onDestroy();
    super.onDestroy();
  }
}
