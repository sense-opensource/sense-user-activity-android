package io.github.senseopensource

import android.app.Activity
import android.content.Context
import android.util.Base64
import io.github.senseopensource.core.getDetails
import io.github.senseopensource.core.getSenseId
import io.github.senseopensource.utils.Location
import io.github.senseopensource.utils.PermissionManager
import org.json.JSONObject
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class Sense private constructor(val activity: Activity) {
    private var initData: SenseOSConfig? = null
    companion object {
        private var instance: Sense? = null
        fun initSDK(activity: Activity, initData: SenseOSConfig) {
            if (instance == null) {
                instance = Sense(activity)
            }
            instance?.apply {
                this.initData = initData
                if (initData.allowGeoLocation == true) {
                    if (PermissionManager().checkLocationPermission(activity)) {
                        Location(activity).getCurrentLocation()
                    }
                }
            }
        }

        private fun saveSenseIdentifier(activity: Activity, key: String, value: String) {
            val sharedPref = activity.getSharedPreferences("SensePrefs", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun getSenseDetails(listener: SenseListener) {
            val sdk = instance
            val deviceDetails = sdk?.let { getDetails(it.activity) }
            val idDetails = sdk?.let { getSenseId(it.activity) }

            val senseId = Hashing().hashSenseId(idDetails.toString().encodeToByteArray())
            val parameters = JSONObject(
                mapOf(
                    "device_details" to deviceDetails,
                    "sense_id" to senseId
                )
            )
            listener.onSuccess(parameters.toString())
        }
    }

    interface SenseListener {
        fun onSuccess(data: String)
        fun onFailure(message: String)
    }
}
