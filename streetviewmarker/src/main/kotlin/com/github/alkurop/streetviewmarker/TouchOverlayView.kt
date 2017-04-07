package com.github.alkurop.streetviewmarker

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import java.util.jar.Attributes

/**
 * Created by alkurop on 13.06.16.
 */
class TouchOverlayView : FrameLayout {
    var onTouchListener:((ev: MotionEvent?) ->Unit)? = null
    @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, style:Int = 0): super(context,attr, style )

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        onTouchListener?.invoke(event)
        return false
    }

    override fun onDraw(canvas: Canvas?) {

    }

}