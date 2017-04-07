package com.github.alkurop.streetviewmarker;

import android.os.Parcelable;
import android.support.annotation.DrawableRes;

/**
 * Created by alkurop on 2/3/17.
 */

public interface Place extends Parcelable {

    String getId ();

    Location getLocation ();

    String getMarkerPath();

    @DrawableRes int getDrawable ();
}
