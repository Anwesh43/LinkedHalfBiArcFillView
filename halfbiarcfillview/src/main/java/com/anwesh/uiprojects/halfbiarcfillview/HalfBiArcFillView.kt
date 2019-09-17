package com.anwesh.uiprojects.halfbiarcfillview

/**
 * Created by anweshmishra on 17/09/19.
 */

import android.content.Context
import android.app.Activity
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Path
import android.graphics.Color
import android.view.View
import android.view.MotionEvent

val nodes : Int = 5
val parts : Int = 2
val scGap : Float = 0.01f / parts
val sizeFactor : Float = 2.9f
val offColor : Int = Color.parseColor("#1565C0")
val onColor : Int = Color.parseColor("#f44336")
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 30

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawHalfBiArcFill(i : Int, size : Float, sc : Float, paint : Paint) {
    val sci : Float = sc.divideScale(i, parts)
    save()
    val path : Path = Path()
    path.addCircle(0f, 0f, size, Path.Direction.CW)
    clipPath(path)
    save()
    scale(1f, 1f - 2 * i)
    paint.color = offColor
    drawRect(RectF(-size, size * sci, size, size), paint)
    paint.color = onColor
    drawRect(RectF(-size, size, size, size * sci), paint)
    restore()
    restore()
}

fun Canvas.drawBiArcCircles(size : Float, sc : Float, paint : Paint) {
    for (j in 0..(parts - 1)) {
        drawHalfBiArcFill(j, size, sc, paint)
    }
}

fun Canvas.drawHBAFNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / sizeFactor
    save()
    translate(gap * (i + 1), h / 2)
    drawBiArcCircles(size, scale, paint)
    restore()
}

class HalfBiArcFillView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }
}
