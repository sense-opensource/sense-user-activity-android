package io.github.senseopensource
import android.annotation.SuppressLint
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.widget.ScrollView

@SuppressLint("ClickableViewAccessibility")
internal class ScrollMetricsTracker(private val scrollView: ScrollView) : View.OnTouchListener {
    private var lastScrollY = 0f
    private var lastTime = 0L
    private val scrollPositions = mutableListOf<Map<String, Float>>()
    private val scrollSpeeds = mutableListOf<Map<String, Float>>()
    private val accelerations = mutableListOf<Map<String, Float>>()
    private val directions = mutableListOf<String>()

    init {
        scrollView.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event == null) return false

        val currentY = scrollView.scrollY.toFloat()
        val currentTime = SystemClock.elapsedRealtime()

        if (event.action == MotionEvent.ACTION_MOVE) {
            if (lastTime != 0L) {
                val timeDiff = (currentTime - lastTime) / 1000.0
                val speedY = (currentY - lastScrollY) / timeDiff
                val accelerationY = (speedY - lastScrollY) / timeDiff

                scrollSpeeds.add(mapOf("x" to 0.0f, "y" to speedY.toFloat()))
                accelerations.add(mapOf("x" to 0.0f, "y" to accelerationY.toFloat()))

                val direction = when {
                    currentY > lastScrollY -> "Scrolling Down"
                    currentY < lastScrollY -> "Scrolling Up"
                    else -> "Static"
                }
                directions.add(direction)
            }

            scrollPositions.add(mapOf("x" to 0.0f, "y" to currentY))
            lastScrollY = currentY
            lastTime = currentTime
        }
        return false
    }

    fun getScrollData(): Map<String, Any> {
        return mapOf(
            "scrollPositions" to scrollPositions,
            "scrollSpeeds" to scrollSpeeds,
            "acceleration" to accelerations,
            "direction" to directions
        )
    }
}
