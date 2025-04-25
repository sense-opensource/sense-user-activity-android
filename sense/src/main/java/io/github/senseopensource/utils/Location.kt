package io.github.senseopensource.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import java.util.*


internal class Location(private val activity: Activity) {

    private var fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
    private var locationRequest: LocationRequest = LocationRequest.create()

    init {
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    @SuppressLint("MissingPermission")
    private fun checkSettingForGPS() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(activity, 101)
                } catch (ex: IntentSender.SendIntentException) {
                    ex.printStackTrace()
                }
            }
        }
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            for (location in locationResult.locations) {
                getCurrentLocation()
            }
            fusedLocationProviderClient.removeLocationUpdates(this)
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        try {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                if (it != null){
                    val location: Location = it
                    val geoCoder = Geocoder(activity, Locale.getDefault())
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            geoCoder.getFromLocation(location.latitude, location.longitude, 1, Geocoder.GeocodeListener {result ->
                                val updatedAddress: List<Address> = result
                                Constants.address = updatedAddress[0].getAddressLine(0)
                            })
                        }else{
                            val addr: MutableList<Address>? = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                             Constants.address = addr?.get(0)?.getAddressLine(0).toString()
                        }
                        Constants.latitude = location.latitude.toString()
                        Constants.longitude = location.longitude.toString()
                    } catch (_: Exception) {}
                }else{
                    checkSettingForGPS()
                }
            }
        }catch (_: Exception){}
    }
}