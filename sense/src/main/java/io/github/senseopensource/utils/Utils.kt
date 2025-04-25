package io.github.senseopensource.utils


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.KeyguardManager
import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.display.DisplayManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaDrm
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.os.StatFs
import android.os.SystemClock
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.format.Formatter
import android.util.Base64
import android.util.DisplayMetrics
import android.view.InputDevice
import android.view.InputDevice.SOURCE_DPAD
import android.view.InputDevice.SOURCE_GAMEPAD
import android.view.InputDevice.SOURCE_JOYSTICK
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigInteger
import java.net.InetAddress
import java.net.NetworkInterface
import java.security.MessageDigest
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import kotlin.math.pow
import kotlin.math.sqrt
import android.database.Cursor

internal fun getIPAddress(): String {
    try {
        val interfaces: List<NetworkInterface> =
            Collections.list(NetworkInterface.getNetworkInterfaces())
        for (inter in interfaces) {
            val addresses: List<InetAddress> = Collections.list(inter.inetAddresses)
            for (address in addresses) {
                if (!address.isLoopbackAddress) {
                    val sAddress: String? = address.hostAddress
                    if (sAddress != null) {
                        val isIPv4 = sAddress.indexOf(':') < 0
                        try {
                            if (isIPv4) {
                                return sAddress
                            } else {
                                val data = sAddress.indexOf('%')
                                if (data < 0) sAddress.uppercase() else sAddress.substring(0, data)
                                    .uppercase()
                            }
                        }catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    } catch (_: Exception) { }
    return ""
}

internal fun isEmulator(): Boolean {
    return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
            || Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.HARDWARE.contains("goldfish")
            || Build.HARDWARE.contains("ranchu")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.PRODUCT.contains("sdk_google")
            || Build.PRODUCT.contains("google_sdk")
            || Build.PRODUCT.contains("sdk")
            || Build.PRODUCT.contains("sdk_x86")
            || Build.PRODUCT.contains("sdk_gphone64_arm64")
            || Build.PRODUCT.contains("vbox86p")
            || Build.PRODUCT.contains("emulator")
            || Build.PRODUCT.contains("simulator"))
}

@SuppressLint("HardwareIds")
internal fun getDeviceId(activity: Activity): String {
    return try {
        Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)
    }catch (e: Exception) {
        ""
    }
}

internal fun getMediaDrmId(): String? {
    return try {
        val widevineUUID = UUID.fromString("edef8ba9-79d6-4ace-a3c8-27dcd51d21ed")
        val mediaDrm = MediaDrm(widevineUUID)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mediaDrm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID).let {
                Base64.encodeToString(it, Base64.NO_WRAP)
            }
        } else {
            val uniqueId = mediaDrm.getPropertyByteArray("deviceUniqueId")
            Base64.encodeToString(uniqueId, Base64.NO_WRAP)
        }
    } catch (e: Exception) {
        null
    }
}
internal fun getGsfId(context: Context): String? {
    val uri = Uri.parse("content://com.google.android.gsf.gservices")
    val idKey = "android_id"
    val params = arrayOf(idKey)
    var cursor: Cursor? = null
    return try {
        cursor = context.contentResolver.query(uri, null, null, params, null)
        if (cursor != null && cursor.moveToFirst() && cursor.columnCount >= 2) {
            val gsfId = java.lang.Long.toHexString(cursor.getString(1).toLong())
            gsfId.uppercase()
        } else {
            null
        }
    } catch (e: Exception) {
        null
    } finally {
        cursor?.close()
    }
}

@SuppressLint("NewApi", "MissingPermission")
internal fun isDataEnabled(activity: Activity): String {
    return try {
        if (getTelephonyManager(activity) != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getTelephonyManager(activity)?.isDataEnabled.toString()
        } else {
            "false"
        }
    }catch (e: Exception) {
        ""
    }
}
@SuppressLint("NewApi", "MissingPermission", "HardwareIds")
internal fun getSimSerialNumber(context: Context): String {
    val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    return androidId
}

@SuppressLint("NewApi", "MissingPermission")
internal fun getNetworkType(context: Context): String {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return "No network"
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return "Unknown network"

        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobile data"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Unknown network"
        }
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo ?: return "No network"

        return when (networkInfo.type) {
            ConnectivityManager.TYPE_WIFI -> "Wi-Fi"
            ConnectivityManager.TYPE_MOBILE -> "Mobile data"
            ConnectivityManager.TYPE_ETHERNET -> "Ethernet"
            else -> "Unknown network"
        }
    }
}

internal fun getMemoryInfo(activity: Activity): Map<String, Any> {
    return try {
        val memoryInfo = ActivityManager.MemoryInfo()
        (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(memoryInfo)
        mapOf(
            "totalMemory" to Formatter.formatFileSize(activity, memoryInfo.totalMem),
            "availableMemory" to Formatter.formatFileSize(activity, memoryInfo.availMem),
            "usedMemory" to Formatter.formatFileSize(activity, memoryInfo.totalMem - memoryInfo.availMem)
        )
    }catch (e: Exception) {
        emptyMap()
    }
}

internal fun getTotalMemory(activity: Activity): String {
    return try {
        val memoryInfo = ActivityManager.MemoryInfo()
        (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(memoryInfo)
        Formatter.formatFileSize(activity, memoryInfo.totalMem)
    }catch (e: Exception) {
        ""
    }
}

internal fun getOsName(activity: Activity): String {
    var codeName = "UNKNOWN"
    try {
        val fields = Build.VERSION_CODES::class.java.fields
        fields.filter { it.getInt(Build.VERSION_CODES::class) == Build.VERSION.SDK_INT }
            .forEach { codeName = "Android " + it.name }
    }catch (_: Exception) {}
    return codeName
}

internal  fun getKernelVersion(): String {
    return try {
        System.getProperty("os.version")!!.toString()
    }catch (e: Exception) {
        ""
    }
}

@RequiresApi(Build.VERSION_CODES.N)
internal fun getProximitySensor(activity: Activity): Map<String, Any> {
    val mSensorManager = activity.getSystemService(SENSOR_SERVICE) as SensorManager
    val mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    if (mSensor == null) {
        return emptyMap();
    } else{
        return try {
            mapOf(
                "id" to if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) mSensor.id else 0,
                "isDynamicSensor" to if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) mSensor.isDynamicSensor else false,
                "isWakeupSensor" to mSensor.isWakeUpSensor,
                "name" to mSensor.name,
                "resolution" to mSensor.resolution,
                "type" to mSensor.type,
                "stringType" to mSensor.stringType,
                "reportingMode" to mSensor.reportingMode,
                "vendor" to mSensor.vendor,
                "version" to mSensor.version,
                "power" to mSensor.power,
                "maxDelay" to mSensor.maxDelay,
                "minDelay" to mSensor.minDelay,
                "maximumRange" to mSensor.maximumRange,
                "proximitySensor" to isProximitySensorAvailable(activity)
            )
        }catch (e: Exception) {
            emptyMap()
        }
    }
}

@SuppressLint("NewApi", "MissingPermission")
internal fun isRoamingEnabled(activity: Activity): String {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val telephonyManager = activity.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            if (telephonyManager != null) {
                val method = TelephonyManager::class.java.getMethod("isDataRoamingEnabled")
                method.isAccessible = true
                val result = method.invoke(telephonyManager) as? Boolean
                result?.toString() ?: "false"
            } else {
                "false"
            }
        } else {
            "false"
        }
    }catch (e: Exception) {
        "false"
    }
}

internal fun getBatteryLevel(activity: Activity): String {
    val batteryManager = activity.getSystemService(BATTERY_SERVICE) as BatteryManager
    return try {
        "${batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)}%" // Append the % symbol
    } catch (e: Exception) {
        ""
    }
}

@RequiresApi(Build.VERSION_CODES.M)
internal fun isDeviceCharging(activity: Activity): Boolean {
    return try {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = activity.registerReceiver(null, intentFilter)
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
    } catch (e: Exception) {
        false
    }
}

internal fun getTelephonyManager(activity: Activity): TelephonyManager? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        } else {
            null;
        }
    }catch (e: NoSuchMethodError) {
        null;
    }
}

internal fun getDeviceLanguage(): Map<String, String> {
    val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Resources.getSystem().configuration.locales[0]
    }else {
        Resources.getSystem().configuration.locale
    }
    return mapOf(
        "language" to locale.language,
        "displayLanguage" to locale.displayLanguage,
        "iso3Language" to locale.isO3Language
    )
}

internal fun getDeviceCountry(context: Context): Map<String, String> {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val countryCode = telephonyManager.networkCountryIso
    val locale = Locale("", countryCode)

    return mapOf(
        "country" to countryCode,
        "displayCountry" to locale.displayCountry,
        "iso3Country" to locale.isO3Country
    )
}
internal fun getDeviceLocation(context: Activity): Map<String, String> {
    val localeCountryCode = Locale.getDefault().country
    val locale = Locale("", localeCountryCode)
    val geocoder = Geocoder(context, Locale.getDefault())
    var addressString = Constants.address
    var latitude = Constants.latitude
    var longitude = Constants.longitude
    var country_code = localeCountryCode
    try {
        val addresses: List<Address>? =  geocoder.getFromLocationName("Constants.address", 1)
        if (!addresses.isNullOrEmpty()) {
            val address: Address = addresses[0]
            latitude = address.latitude.toString()
            longitude = address.longitude.toString()
            addressString = address.getAddressLine(0)
            country_code = address.countryCode ?: Locale.getDefault().country

        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return mapOf(
        "latitude" to latitude,
        "longitude" to longitude,
        "address" to addressString,
        "countryCode" to country_code,
        "displayCountry" to locale.displayCountry,
        "iso3Country" to locale.isO3Country,
        "timezone" to TimeZone.getDefault().id
    )
}

internal fun getStorageInfo(activity: Activity): Map<String, Any> {

    val stat = StatFs(Environment.getDataDirectory().path)
    val totalSpace = stat.totalBytes
    val availableSpace = stat.availableBytes
    val usedSpace = totalSpace - availableSpace
    val totalSpaceInGb = totalSpace / 1073741824.0
    val usedSpaceInGb = usedSpace / 1073741824.0
    val availableSpaceInGb = availableSpace / 1073741824.0
    val formattedTotalSpace = String.format("%.2f", totalSpaceInGb)
    val formattedUsedSpace = String.format("%.2f", usedSpaceInGb)
    val formattedAvailableSpace = String.format("%.2f", availableSpaceInGb)
    return mapOf(
        "total" to formattedTotalSpace,
        "used" to formattedUsedSpace,
        "free" to formattedAvailableSpace
    )
}

internal fun getScreenBrightness(activity: Activity): Int {
    return try {
        Settings.System.getInt(activity.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
    } catch (e: Exception) {
        -1
    }
}

internal fun getScreenSize(activity: Activity): String {
    return try {
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        val x = (dm.widthPixels / dm.xdpi).toDouble().pow(2.0)
        val y = (dm.heightPixels / dm.ydpi).toDouble().pow(2.0)
        val screenSize = sqrt(x + y)
        String.format("%.2f", screenSize) // Format to 2 decimal places
    } catch (e: Exception) {
        ""
    }
}

internal fun getScreenResolution(activity: Activity): String {
    return try {
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        "${dm.widthPixels} x ${dm.heightPixels}"
    }catch (e: Exception) {
        ""
    }
}

internal fun getDevicePixelRatio(context: Context): Float {
    val displayMetrics = context.resources.displayMetrics
    return displayMetrics.density
}

internal fun getScreenWidth(activity: Activity): Int {
    return try {
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        dm.widthPixels
    } catch (e: Exception) {
        0
    }
}

internal fun getScreenHeight(activity: Activity): Int {
    return try {
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        dm.heightPixels
    } catch (e: Exception) {
        0
    }
}

internal fun getDeviceOrientation(activity: Activity): String {
    return try {
        val orientation: Int = activity.resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            "landscape"
        } else {
            "portrait"
        }
    }catch (e: Exception) {
        ""
    }
}

internal fun isSpeakerAvailable(activity: Activity): Boolean {
    return try {
        val audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        val packageManager: PackageManager = activity.packageManager
        if (audioManager!!.isBluetoothA2dpOn) {
            true
        } else if (audioManager.isBluetoothScoOn) {
            true
        } else if (audioManager.isWiredHeadsetOn) {
            true
        } else if (audioManager.isSpeakerphoneOn) {
            true
        } else packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)
    }catch (e: Exception) {
        false
    }
}

internal fun isTouchScreen(activity: Activity): Boolean {
    return try {
        activity.packageManager.hasSystemFeature("android.hardware.touchscreen")
    }catch (e: Exception) {
        false
    }
}

internal fun getHashValue(data: MutableMap<String, Any?>): String {
    val md = MessageDigest.getInstance("MD5")
    val bigInt = BigInteger(1, md.digest(data.toString().toByteArray(Charsets.UTF_8)))
    return String.format("%032x", bigInt)
}

internal fun String.encode(): String{
    return Base64.encodeToString(this.toByteArray(charset("UTF-8")), Base64.DEFAULT)
}

internal fun isPasscodeEnabled(context: Context): Boolean {
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    return keyguardManager.isKeyguardSecure
}

internal fun getKernelArchitecture(): String {
    val arch = System.getProperty("os.arch")
    return if (arch.contains("arm")) {
        "ARM"
    } else if (arch.contains("aarch64") || arch.contains("arm64")) {
        "ARM64"
    } else if (arch.contains("x86_64")) {
        "x86_64"
    } else {
        "Unknown"
    }
}

internal fun getKernelName(): String? {
    return System.getProperty("os.name")
}

internal fun getBatteryTemperature(context: Context): String {
    val intent: Intent? = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    val temperature: Int = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
    return "${temperature / 10f} °C"
}

internal fun getBatteryVoltage(context: Context): String {
    val intent: Intent? = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    val voltage: Int = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0
    return "${voltage / 1000f} V"
}

internal fun getBuildDevice(): String {
     val deviceModel: String = Build.MODEL
     return deviceModel
}

internal fun buildManufacturer(): String {
     val manufacturer: String = Build.MANUFACTURER
    return manufacturer
}

internal fun getScreenColorDepth(context: Context): Int {
    val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val pixelColor = bitmap.getPixel(0, 0)
    val alpha = Color.alpha(pixelColor)
    val red = Color.red(pixelColor)
    val green = Color.green(pixelColor)
    val blue = Color.blue(pixelColor)
    return when {
        alpha == 255 && red in 0..255 && green in 0..255 && blue in 0..255 -> {
            32
        }
        red in 0..255 && green in 0..255 && blue in 0..255 -> {
            24
        }
        else -> {
            -1
        }
    }
}

internal fun getCpuSpeed(): String {
     var cpuSpeed = ""
     try {
         val br = BufferedReader(FileReader("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"))
         cpuSpeed = br.readLine()
         br.close()
     } catch (e: Exception) {
         e.printStackTrace()
     }
     return cpuSpeed
}

internal fun isUiAutomatorInstalled(): Boolean {
    try {
        Class.forName("androidx.test.uiautomator.UiDevice")
        Class.forName("androidx.test.uiautomator.UiObject")
        Class.forName("androidx.test.uiautomator.UiSelector")
        return true
    } catch (e: ClassNotFoundException) {
        return false
    }
}

internal fun isOnCall(context: Context): Boolean {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    if (ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_PHONE_STATE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            (context as Activity),
            arrayOf(Manifest.permission.READ_PHONE_STATE),
            1001
        )
        return false
    }
    return telephonyManager.callState != TelephonyManager.CALL_STATE_IDLE
}

internal fun isRemoteControlConnected(): Boolean {
    val deviceIds = InputDevice.getDeviceIds()
    for (deviceId in deviceIds) {
        val inputDevice = InputDevice.getDevice(deviceId)
        inputDevice?.let {
            if ((it.sources and SOURCE_DPAD == SOURCE_DPAD) ||
                (it.sources and SOURCE_GAMEPAD == SOURCE_GAMEPAD) ||
                (it.sources and SOURCE_JOYSTICK == SOURCE_JOYSTICK)
            ) {
                return true
            }
        }
    }
    return false
}

internal fun isGPSEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

internal fun isAudioMuted(context: Context): Boolean {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0
}
internal fun getCurrentVolumeLevel(context: Context): Int {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
}

internal fun getCpuCount(): Int {
    return Runtime.getRuntime().availableProcessors()
}

internal fun isPlayStoreEnabled(context: Context): Boolean {
    val packageManager = context.packageManager
    return try {
        val playStorePackageInfo = packageManager.getPackageInfo("com.android.vending", 0)
        playStorePackageInfo.applicationInfo.enabled
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

internal fun getCpuType(): String {
    var cpuType = ""
    try {
        val process = ProcessBuilder()
            .command("/system/bin/cat", "/proc/cpuinfo")
            .redirectErrorStream(true)
            .start()
        val inputStream = process.inputStream
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            if (line!!.contains("Processor")) {
                cpuType = line!!.substring(line!!.indexOf(":") + 1).trim()
                break
            }
        }
        bufferedReader.close()
        inputStreamReader.close()
        inputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return cpuType
}

internal fun getLastBootTime(): Date {
    val currentTime = System.currentTimeMillis()
    val uptime = SystemClock.elapsedRealtime()
    val lastBootTime = currentTime - uptime
    return Date(lastBootTime)
}

internal fun getSystemUptime(): Long {
    return SystemClock.elapsedRealtime()
}

internal fun getUptimeWithoutDeepSleep(): Long {
    return SystemClock.uptimeMillis()
}

internal fun getSystemProperty(key: String): String? {
    return try {
        val systemProperties = Class.forName("android.os.SystemProperties")
        val getMethod = systemProperties.getMethod("get", String::class.java)
        getMethod.invoke(systemProperties, key) as String
    } catch (e: Exception) {
        null
    }
}

internal fun getSoftwareChannel(): String? {
    val keys = listOf(
        "ro.build.flavor",
        "ro.build.version.incremental",
        "ro.build.display.id",
        "ro.product.name",
        "ro.product.device"
    )

    for (key in keys) {
        val value = getSystemProperty(key)
        if (!value.isNullOrEmpty()) {
            return value
        }
    }
    return null
}

@RequiresApi(Build.VERSION_CODES.N)
internal fun getNetworkStatus(context: Context): Any {
    try {
        if (isWiFiConnected(context)) {
            val ssid = getWiFiSSID(context)
            if (ssid != null && isMobileHotspot(ssid)) {
                return "Mobile Hotspot"
            } else {
                return "Wi-Fi"
            }
        }
        if (isEthernetConnected(context)) {
            return "Ethernet(LAN)"
        }
        val cellularInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getCellularNetworkType(context)
        } else {
            return "Unknown"
        }
        if (cellularInfo != null) {
            return cellularInfo
        } else {
            return "Not Available"
        }
    } catch (e: SecurityException) {
        e.printStackTrace()
        return "Not Available"
    }
}

@RequiresApi(Build.VERSION_CODES.M)
private fun isWiFiConnected(context: Context): Boolean {
    return try {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    } catch (e: SecurityException) {
        e.printStackTrace()
        false
    }
}

@RequiresApi(Build.VERSION_CODES.M)
private fun isEthernetConnected(context: Context): Boolean {
    return try {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    } catch (e: SecurityException) {
        e.printStackTrace()
        false
    }
}

@RequiresApi(Build.VERSION_CODES.N)
private fun getCellularNetworkType(context: Context): String? {
    return try {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        when (telephonyManager.dataNetworkType) {
            TelephonyManager.NETWORK_TYPE_LTE -> "4G"
            TelephonyManager.NETWORK_TYPE_NR -> "5G"
            TelephonyManager.NETWORK_TYPE_UNKNOWN -> null
            else -> "Unknown Cellular"
        }
    } catch (e: SecurityException) {
        e.printStackTrace()
        null
    }
}

private fun isMobileHotspot(ssid: String): Boolean {
    val hotspotIdentifiers = listOf(
        "iPhone",
        "Android Hotspot",
        "My Mobile Hotspot",
        "Mobile Hotspot"
    )
    return hotspotIdentifiers.any { ssid.contains(it, ignoreCase = true) }
}
@RequiresApi(Build.VERSION_CODES.N)
internal fun getWiFiSSID(context: Context): String? {
    return try {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        if(wifiInfo.ssid.contains("unknown")) {
            return "Unknown"
        } else {
            wifiInfo.ssid?.removePrefix("\"")
                ?.removeSuffix("\"")
        }
    } catch (e: SecurityException) {
        null
    }
}


@RequiresApi(Build.VERSION_CODES.N)
internal fun getNetwork(context: Context): String {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
        return "Unknown"
    }

    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val networkType = telephonyManager.dataNetworkType

    return when (networkType) {
        TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE -> "2G"
        TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_HSDPA,
        TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA,
        TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EVDO_0,
        TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_EVDO_B -> "3G"
        TelephonyManager.NETWORK_TYPE_LTE -> "4G"
        TelephonyManager.NETWORK_TYPE_NR -> "5G"
        else -> "Unknown"
    }
}

internal fun displaySoftwareChannel(): String? {
    val softwareChannel = getSoftwareChannel()
    return softwareChannel
}

internal fun isBatterySaverModeOn(context: Context): Boolean {
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return powerManager.isPowerSaveMode
}

internal fun getVolumeLevels(context: Context): Map<String, Int> {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    return mapOf(
        "mediaVolume" to audioManager.getStreamVolume(AudioManager.STREAM_MUSIC),
        "callVolume" to audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL),
        "ringVolume" to audioManager.getStreamVolume(AudioManager.STREAM_RING)
    )
}

internal fun isProximitySensorAvailable(context: Context): Boolean {
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val proximitySensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    return proximitySensor != null
}

@SuppressLint("HardwareIds")
internal fun getDeviceSerialNumber(context: Context): String {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                Build.getSerial()
            } else {
                "Permission not granted"
            }
        } else {
            Build.SERIAL
        }
    } catch (e: SecurityException) {
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    } catch (e: Exception) {
        "Error retrieving identifier"
    }
}
internal fun btoa(input: String): String {
    return Base64.encodeToString(input.toByteArray(), Base64.DEFAULT)
}
internal fun isScreenBeingMirrored(context: Context): Boolean {
    val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    val presentationDisplays = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)
     return if (detectVirtualDisplays(context) == true) {
         false
     } else {
         presentationDisplays.isNotEmpty()
     }
 }

internal fun detectVirtualDisplays(context: Context): Boolean {
    val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    val displays = displayManager.displays
    var displayInfo : Boolean = false
    for (display in displays) {
        if(display.name.contains("screencap") || display.name.contains("teamviewer")){
            displayInfo = true
        }
    }
    return displayInfo;
}