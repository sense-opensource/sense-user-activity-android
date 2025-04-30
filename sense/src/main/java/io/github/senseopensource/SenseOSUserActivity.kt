package io.github.senseopensource

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.view.View
import android.widget.EditText
import android.widget.ScrollView
import io.github.senseopensource.core.KeystrokeTracker

class SenseOSUserActivity private constructor(private val activity: Activity) {
    init {
        proximityHelper = ProximitySensorHelper(activity).also { it.startListening() }
        tiltHelper = TiltSensorHelper(activity).also { it.startListening() }
        gyroHelper = GyroscopeHelper(activity).also { it.startListening() }
    }
    companion object {
        private var instance: SenseOSUserActivity? = null
        private lateinit var touchTracker: TouchscreenTracker
        private lateinit var scrollTracker: ScrollMetricsTracker

        private lateinit var proximityHelper: ProximitySensorHelper
        private lateinit var tiltHelper: TiltSensorHelper
        private lateinit var gyroHelper: GyroscopeHelper

        fun initSDK(activity: Activity){
            if (instance == null) {
                instance = SenseOSUserActivity(activity)
            }
        }
        fun stopSensors() {
            proximityHelper.stopListening()
            tiltHelper.stopListening()
            gyroHelper.stopListening()
        }

        fun initKeyStrokeBehaviour(context: Context, vararg editTexts: EditText) {
            KeystrokeTracker.initKeyStrokeBehaviour(context, *editTexts)
        }

        fun initTouchBehaviour(context: Context, view: View) {
            touchTracker = TouchscreenTracker(context, view)
        }

        fun initScrollBehaviour(view: ScrollView) {
            scrollTracker = ScrollMetricsTracker(view)
        }

        fun getBehaviourData(): Map<String, Any?> {
            val data = mapOf(
                "keyStrokeData" to KeystrokeTracker.getKeyStrokes(),
                "orientationData" to getOrientationData(),
                "touchScreenData" to touchTracker.getTouchscreenData(),
                "scrollMetrics" to scrollTracker.getScrollData()
            )
            stopSensors();
            return mapOf(
               "user_activity" to data
            )
        }

        private fun getOrientationMode(): String {
            return when (Resources.getSystem().configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> "Landscape Left"
                Configuration.ORIENTATION_PORTRAIT -> "Portrait"
                else -> "Unknown"
            }
        }

        private fun getOrientationData(): Map<String, Any> {
            val orientationMode = getOrientationMode()
            return mapOf(
                "mode" to listOf(orientationMode),
                "proximityData" to listOf(proximityHelper.proximityData),
                "tiltPosition" to listOf(tiltHelper.tiltPosition),
                "gyroscope" to gyroHelper.gyroscopeData
            )
        }
    }
}
