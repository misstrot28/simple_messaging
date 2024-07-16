package com.streamline3.simplemessaging.server

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * Class for utility functions.
 *
 * @author Nirson Samson
 * @date 07.16.2024
 */

object Utils {

    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime(): String {
        return SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(Date())
    }
}