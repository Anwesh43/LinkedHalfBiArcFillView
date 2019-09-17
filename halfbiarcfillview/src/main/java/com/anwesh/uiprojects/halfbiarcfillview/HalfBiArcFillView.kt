package com.anwesh.uiprojects.halfbiarcfillview

/**
 * Created by anweshmishra on 17/09/19.
 */

import android.content.Context
import android.app.Activity
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Color
import android.view.View
import android.view.MotionEvent

val nodes : Int = 5
val parts : Int = 4
val scGap : Float = 0.01f
val strokeFactor : Float = 90f
val sizeFactor : Float = 2.9f
val offColor : Int = Color.parseColor("#1565C0")
val onColor : Int = Color.parseColor("#f44336")
val backColor : Int = Color.parseColor("#BDBDBD")
