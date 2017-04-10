package com.github.alkurop.streetviewmarker;

import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by alkurop on 2/3/17.
 */

public interface Place extends Parcelable {

    String getId ();

    LatLng getLocation ();

    String getMarkerPath();

    @DrawableRes int getDrawable ();
}
