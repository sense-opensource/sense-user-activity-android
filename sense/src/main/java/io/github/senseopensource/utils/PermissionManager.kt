package io.github.senseopensource.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

internal class PermissionManager {

    private val permissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE
    )
    private val permissionsLocation = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    fun checkPermission(activity: Activity): Boolean {
        return ActivityCompat.checkSelfPermission(activity, permissions[0]) == PackageManager.PERMISSION_GRANTED
    }

    fun checkLocationPermission(activity: Activity): Boolean {
        return ActivityCompat.checkSelfPermission(activity, permissionsLocation[0]) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, permissionsLocation[1]) == PackageManager.PERMISSION_GRANTED
    }

    fun askPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, permissions, 123)
    }

    fun askLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, permissionsLocation, 1001)
    }

    fun handlePermissionResult(
        requestCode: Int,
        grantResults: IntArray,
        onPermissionGranted: () -> Unit
    ) {
        if (requestCode == 123 || requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults.any { it == PackageManager.PERMISSION_GRANTED }) {
                onPermissionGranted()
            }
        }
    }
}
