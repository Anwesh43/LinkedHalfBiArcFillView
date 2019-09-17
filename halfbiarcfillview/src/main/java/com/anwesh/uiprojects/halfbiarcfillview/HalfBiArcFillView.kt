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

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class HBAFNode(var i : Int, val state : State = State()) {

        private var prev : HBAFNode? = null
        private var next : HBAFNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = HBAFNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas?.drawHBAFNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : HBAFNode {
            var curr : HBAFNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class HalfBiArcFill(var i : Int) {

        private val root : HBAFNode = HBAFNode(0)
        private var curr : HBAFNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : HalfBiArcFillView) {

        private val animator : Animator = Animator(view)
        private val hbaf : HalfBiArcFill = HalfBiArcFill(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            hbaf.draw(canvas, paint)
            animator.animate {
                hbaf.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            hbaf.startUpdating {
                animator.start()
            }
        }
    }
}
