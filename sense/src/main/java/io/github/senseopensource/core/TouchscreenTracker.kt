package io.github.senseopensource
import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import kotlin.math.atan2

@SuppressLint("ClickableViewAccessibility")
class TouchscreenTracker(context: Context, private val view: View) {
    private val gestureDetector = GestureDetector(context, GestureListener())
    private val scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())

    private val pressureLevels = mutableListOf<Float>()
    private val tapBehavior = mutableListOf<Long>()
    private val holdPatterns = mutableListOf<String>()
    private val swipeSpeeds = mutableListOf<Map<String, Float>>()
    private val swipeDurations = mutableListOf<Long>()
    private val swipeDirections = mutableListOf<String>()
    private val rotationAngles = mutableListOf<Float>()
    private val pinchGestures = mutableListOf<Float>()
    private val handedness = mutableListOf<String>()

    private var lastTapTime: Long = 0
    private var longPressDetected = false
    private var lastSwipeTime: Long = 0

    init {
        view.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            scaleGestureDetector.onTouchEvent(event)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastTapTime = System.currentTimeMillis()
                    pressureLevels.add(event.pressure)
                    longPressDetected = false
                }
                MotionEvent.ACTION_UP -> {
                    val tapDuration = System.currentTimeMillis() - lastTapTime
                    tapBehavior.add(tapDuration)
                    if (longPressDetected) {
                        holdPatterns.add("Long Press Detected")
                    }
                }
            }
            true
        }
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onLongPress(e: MotionEvent) {
            longPressDetected = true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 != null && e2 != null) {
                val duration = System.currentTimeMillis() - lastSwipeTime
                lastSwipeTime = System.currentTimeMillis()

                val direction = getSwipeDirection(e1.x, e1.y, e2.x, e2.y)

                swipeSpeeds.add(mapOf("x" to velocityX, "y" to velocityY))
                swipeDurations.add(duration)
                swipeDirections.add(direction)
                handedness.add(if (velocityX > 0) "Right-Handed Use" else "Left-Handed Use")
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            pinchGestures.add(detector.scaleFactor)
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            rotationAngles.add(atan2(detector.currentSpanY, detector.currentSpanX) * (180 / Math.PI).toFloat())
            return true
        }
    }

    private fun getSwipeDirection(x1: Float, y1: Float, x2: Float, y2: Float): String {
        val deltaX = x2 - x1
        val deltaY = y2 - y1
        return when {
            kotlin.math.abs(deltaX) > kotlin.math.abs(deltaY) -> if (deltaX > 0) "Right" else "Left"
            else -> if (deltaY > 0) "Down" else "Up"
        }
    }

    fun getTouchscreenData(): Map<String, Any> {
        return mapOf(
            "name" to "Touchscreen Pressure and Gestures",
            "data" to mapOf(
                "pressureLevels" to pressureLevels,
                "tapBehaviour" to tapBehavior.map { "$it ms" },
                "holdPatterns" to holdPatterns,
                "swipeDynamics" to mapOf(
                    "speed" to swipeSpeeds,
                    "duration" to swipeDurations.map { "$it ms" },
                    "direction" to swipeDirections
                ),
                "multiTouchGestures" to mapOf(
                    "rotation" to rotationAngles.map { "$it°" },
                    "pinchGesture" to pinchGestures
                ),
                "handedness" to handedness
            )
        )
    }
}
