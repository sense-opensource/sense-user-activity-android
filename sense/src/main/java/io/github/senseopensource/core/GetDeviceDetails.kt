package io.github.senseopensource.core
import android.app.Activity
import android.os.Build
import io.github.senseopensource.utils.Constants
import io.github.senseopensource.utils.getDeviceId
import io.github.senseopensource.utils.getIPAddress
import io.github.senseopensource.utils.isEmulator
import io.github.senseopensource.utils.buildManufacturer
import io.github.senseopensource.utils.displaySoftwareChannel
import io.github.senseopensource.utils.getBatteryLevel
import io.github.senseopensource.utils.getBatteryTemperature
import io.github.senseopensource.utils.getBatteryVoltage
import io.github.senseopensource.utils.getBuildDevice
import io.github.senseopensource.utils.getCpuCount
import io.github.senseopensource.utils.getCpuSpeed
import io.github.senseopensource.utils.getCpuType
import io.github.senseopensource.utils.getCurrentVolumeLevel
import io.github.senseopensource.utils.getDeviceCountry
import io.github.senseopensource.utils.getDeviceLanguage
import io.github.senseopensource.utils.getDeviceLocation
import io.github.senseopensource.utils.getDeviceOrientation
import io.github.senseopensource.utils.getDevicePixelRatio
import io.github.senseopensource.utils.getDeviceSerialNumber
import io.github.senseopensource.utils.getGsfId
import io.github.senseopensource.utils.getHashValue
import io.github.senseopensource.utils.getKernelArchitecture
import io.github.senseopensource.utils.getKernelName
import io.github.senseopensource.utils.getKernelVersion
import io.github.senseopensource.utils.getLastBootTime
import io.github.senseopensource.utils.getMediaDrmId
import io.github.senseopensource.utils.getMemoryInfo
import io.github.senseopensource.utils.getNetwork
import io.github.senseopensource.utils.getNetworkStatus
import io.github.senseopensource.utils.getNetworkType
import io.github.senseopensource.utils.getOsName
import io.github.senseopensource.utils.getProximitySensor
import io.github.senseopensource.utils.getScreenBrightness
import io.github.senseopensource.utils.getScreenColorDepth
import io.github.senseopensource.utils.getScreenHeight
import io.github.senseopensource.utils.getScreenResolution
import io.github.senseopensource.utils.getScreenSize
import io.github.senseopensource.utils.getScreenWidth
import io.github.senseopensource.utils.getSimSerialNumber
import io.github.senseopensource.utils.getStorageInfo
import io.github.senseopensource.utils.getSystemUptime
import io.github.senseopensource.utils.getTotalMemory
import io.github.senseopensource.utils.getUptimeWithoutDeepSleep
import io.github.senseopensource.utils.getVolumeLevels
import io.github.senseopensource.utils.getWiFiSSID
import io.github.senseopensource.utils.isAudioMuted
import io.github.senseopensource.utils.isBatterySaverModeOn
import io.github.senseopensource.utils.isDataEnabled
import io.github.senseopensource.utils.isDeviceCharging
import io.github.senseopensource.utils.isGPSEnabled
import io.github.senseopensource.utils.isOnCall
import io.github.senseopensource.utils.isPasscodeEnabled
import io.github.senseopensource.utils.isPlayStoreEnabled
import io.github.senseopensource.utils.isRemoteControlConnected
import io.github.senseopensource.utils.isRoamingEnabled
import io.github.senseopensource.utils.isScreenBeingMirrored
import io.github.senseopensource.utils.isSpeakerAvailable
import io.github.senseopensource.utils.isTouchScreen
import io.github.senseopensource.utils.isUiAutomatorInstalled
import java.util.TimeZone

internal val SOC_MANUFACTURER = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      Build.SOC_MANUFACTURER ?: ""
   } else {
      ""
   }
} else {
   ""
}
internal val SKU = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      Build.SKU ?: ""
   } else {
      ""
   }
} else {
   ""
}
internal fun getSenseId(activity: Activity): MutableMap<String, String?> {
    val device = mutableMapOf(
        "deviceId" to getDeviceId(activity),
        "getMediaDrmId" to getMediaDrmId(),
        "gsfId" to getGsfId(activity)
    )
    return device
}

internal fun getDetails(activity: Activity): MutableMap<String, Any> {
   val device = mutableMapOf(
        "deviceId" to getDeviceId(activity),
        "getMediaDrmId" to getMediaDrmId(),
        "gsfId" to getGsfId(activity),
        "platform" to "android",
        "isRealDevice" to !isEmulator(),
        "touchSupport" to isTouchScreen(activity),
        "deviceMemory" to getTotalMemory(activity),
        "os" to getOsName(activity),
        "deviceTypes" to mapOf(
           "isMobile" to true,
           "isLinux" to false,
           "isLinux64" to false,
           "isSmartTV" to false,
           "isTablet" to false
        ),
        "androidID" to Build.ID,
        "board" to Build.BOARD,
        "brand" to Build.BRAND,
        "display" to Build.DISPLAY,
        "bootLoader" to Build.BOOTLOADER,
        "fingerPrint" to Build.FINGERPRINT,
        "hardware" to Build.HARDWARE,
        "host" to Build.HOST,
        "manufacturer" to Build.MANUFACTURER,
        "model" to Build.MODEL,
        "product" to Build.PRODUCT,
        "user" to Build.USER,
        "type" to Build.TYPE,
        "localIpAddress" to  getIPAddress(),
        "dataEnabled" to  isDataEnabled(activity),
        "memoryInformation" to getMemoryInfo(activity),
        "serialNumber" to  getSimSerialNumber(activity),
        "networkConfig" to getNetworkType(activity),
        "dataRoaming" to isRoamingEnabled(activity),
        "kernelVersion" to getKernelVersion(),
        "proximitySensorData" to if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getProximitySensor(activity)
        } else {emptyMap()},
        "countryInfo" to getDeviceCountry(activity),
        "systemStorage" to getStorageInfo(activity),
        "isOnCall" to isOnCall(activity),
        "buildDevice" to getBuildDevice(),
        "buildManufacture" to buildManufacturer(),
        "cpuSpeed" to getCpuSpeed(),
        "automatorInstalled" to isUiAutomatorInstalled(),
        "screenBeingMirrored" to isScreenBeingMirrored(activity),
        "remoteControlConnected" to isRemoteControlConnected(),
        "isAudioMuted" to isAudioMuted(activity),
        "currentVolumeLevel" to getCurrentVolumeLevel(activity),
        "lastBootTime" to getLastBootTime(),
        "systemUptime" to getSystemUptime(),
        "uptimeWithoutDeepSleep" to getUptimeWithoutDeepSleep(),
        "cpuCount" to getCpuCount(),
        "cpuType" to getCpuType(),
        "passcodeEnabled" to isPasscodeEnabled(activity),
        "kernelArchitecture"  to getKernelArchitecture(),
        "kernelName" to getKernelName(),
        "androidVersion" to Build.VERSION.RELEASE,
        "sdkVersion" to Build.VERSION.SDK_INT,
        "lastSystemUpdate" to if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {Build.VERSION.SECURITY_PATCH} else{""},
        "codeName" to Build.VERSION.CODENAME,
        "softwareChannel" to displaySoftwareChannel(),
        "socManufacture" to SOC_MANUFACTURER,
        "deviceSKU" to SKU,
        "radioVersion" to Build.getRadioVersion(),
        "isGPSEnabled" to isGPSEnabled(activity),
        "isPlayStoreEnabled" to isPlayStoreEnabled(activity),
        "timestamp" to System.currentTimeMillis(),
        "timezone" to TimeZone.getDefault().id,
        "serial" to   getDeviceSerialNumber(activity)
   )
   device["deviceHash"] = getHashValue(device)
   val data = mutableMapOf(
       "version" to "1.0.0",
       "ipAddress_" to Constants.ipAddress,
       "device" to device,
       "media" to mapOf( "volumeStatus" to getVolumeLevels(activity), "audioHardware" to mapOf("hasSpeakers" to isSpeakerAvailable(activity)),
          "microPhoneHardware" to mapOf("hasMicrophone" to true),
          "videoHardware" to mapOf("hasWebCam" to true), ),
       "language" to getDeviceLanguage(),
       "location" to getDeviceLocation(activity),
       "battery" to mapOf("batteryLevel" to getBatteryLevel(activity),"deviceCharging" to if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           isDeviceCharging(activity)
       } else {false},"batteryTemperature" to getBatteryTemperature(activity),
          "batteryVoltage" to getBatteryVoltage(activity),  "isBatterySaveMode" to isBatterySaverModeOn(activity)
       ),
       "screen" to mapOf( "screenWidth" to getScreenWidth(activity),"screenHeight" to getScreenHeight(activity),"screenBrightness" to getScreenBrightness(activity),
          "screenSize" to getScreenSize(activity),"screenOrientation" to getDeviceOrientation(activity),"screenPixelRatio" to getDevicePixelRatio(activity),
          "displayResolution" to getScreenResolution(activity), "colorDepth" to getScreenColorDepth(activity)
       ),
       "zone" to mapOf("timestamp" to System.currentTimeMillis(), "timezone" to TimeZone.getDefault().id)
   )
   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      data["connection"] = mapOf(
         "effectiveType" to getNetwork(activity),
         "wifiSSID" to getWiFiSSID(activity),
         "type" to getNetworkStatus(activity)
      )
   }
   return data
}