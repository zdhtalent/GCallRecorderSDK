package com.geely.callrecord.utils

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

/**
 * @author zdh
 * date 2019/11/27.
 */
object DateUtils {

    const val SHORT = "SHORT"
    const val MEDIUM = "MEDIUM"
    const val FULL = "FULL"

    fun dateToStringDay(date: Date): String? {
        val format: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        return format.format(date)
    }

    fun dateToString(date: Date, type: String): String? {
        val format: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(date)
    }

    fun stringToDate(str: String): Date? {
        val format = SimpleDateFormat("yyyy-MM-dd")
        var date: Date? = null
        try {
            // Fri Feb 24 00:00:00 CST 2012
            date = format.parse(str)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        // 2012-02-24
        date = java.sql.Date.valueOf(str)

        return date
    }
}
