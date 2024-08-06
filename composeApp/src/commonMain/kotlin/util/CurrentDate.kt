package util

import java.text.SimpleDateFormat
import java.util.*

fun GetCurrentDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS")
    return sdf.format(Date())
}