package com.github.alkurop.mylibrary

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.gms.maps.StreetViewPanoramaView

/**
 * Created by alkurop on 2/3/17.
 */
class StreetMarkerView : View {
    lateinit var streetView: StreetViewPanoramaView

    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0)
              : super(context, attrs, defStyleAttr, defStyleRes)
}