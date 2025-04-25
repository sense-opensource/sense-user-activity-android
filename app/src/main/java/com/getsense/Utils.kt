package com.getsense
import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Locale
import android.content.Context
import org.json.JSONObject
import java.util.*
import java.util.TimeZone
import java.util.Date


object Utils{
    private const val PREFS_NAME = "app_prefs"

    fun putString(context: Context, key: String, value: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(key, value).apply()
    }

    fun getString(context: Context, key: String, defaultValue: String? = null): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(key, defaultValue)
    }

    fun clear(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    fun parseIsoDate(dateString: String): Date? {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun formatToTodayOrDateTime(date: Date): String {
        val today = Calendar.getInstance()
        val dateCalendar = Calendar.getInstance().apply {
            time = date
        }

        return if (today.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)) {
            // If the date is today, format as "Today, HH:mm a"
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            "Today, ${timeFormat.format(date)}"
        } else {
            // Otherwise, format as "dd MMM yyyy, HH:mm a"
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            dateFormat.format(date)
        }
    }
    @SuppressLint("DefaultLocale")
    fun getGmtOffset(timeZoneId: String): String {
        val timeZone = TimeZone.getTimeZone(timeZoneId)
        val offsetInMillis = timeZone.rawOffset

        // Get hours and minutes from the offset in milliseconds
        val hours = offsetInMillis / (1000 * 60 * 60)
        val minutes = Math.abs((offsetInMillis / (1000 * 60)) % 60)

        return String.format("GMT%+d:%02d", hours, minutes)
    }
    fun getGmtOffsetWithZoneName(timeZoneId: String): String {
        val timeZone = TimeZone.getTimeZone(timeZoneId)

        // Get the GMT offset in milliseconds
        val offsetInMillis = timeZone.rawOffset
        val hours = offsetInMillis / (1000 * 60 * 60)
        val minutes = Math.abs((offsetInMillis / (1000 * 60)) % 60)

        // Format offset as GMT+/-HH:MM
        val gmtOffset = String.format("GMT%+d:%02d", hours, minutes)

        // Get the display name of the time zone
        val timeZoneName = timeZone.getDisplayName(Locale.ENGLISH)

        // Return the formatted string with offset and time zone name
        return "$gmtOffset ($timeZoneName)"
    }
    fun checkForStringOrTrue(jsonObject: JSONObject, key: String): Boolean {
        // Retrieve the value with optString to avoid exceptions if the key is missing
        val value = jsonObject.optString(key, "")

        // Check if the value is either "true" (case-insensitive) or a non-empty string
        return value.equals("true", ignoreCase = true)
    }




}