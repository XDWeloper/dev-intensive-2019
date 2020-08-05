package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.shortFormat(): String {
    val pattern = if (this.isSameDay(Date())) "HH:mm" else "dd.MM.yy"
    return SimpleDateFormat(pattern, Locale("ru")).format(this)
}

private fun Date.isSameDay(otherDate: Date): Boolean = (this.time / DAY) == (otherDate.time / DAY)

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    this.time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val diff = date.time - this.time
    val absDiff = abs(diff)
    val isPast = diff > 0

    return when {
        absDiff / SECOND <= 1 -> "только что"
        absDiff / SECOND <= 45 -> if (isPast) "несколько секунд назад" else "через несколько секунд"
        absDiff / SECOND <= 75 -> if (isPast) "минуту назад" else "через минуту"
        absDiff / MINUTE <= 45 -> if (isPast) "${TimeUnits.MINUTE.plural((absDiff / MINUTE).toInt())} назад"
        else "через ${TimeUnits.MINUTE.plural((absDiff / MINUTE).toInt())}"
        absDiff / MINUTE <= 75 -> if (isPast) "час назад" else "через час"
        absDiff / HOUR <= 22 -> if (isPast) "${TimeUnits.HOUR.plural((absDiff / HOUR).toInt())} назад"
        else "через ${TimeUnits.HOUR.plural((absDiff / HOUR).toInt())}"
        absDiff / HOUR <= 26 -> if (isPast) "день назад" else "через день"
        absDiff / DAY <= 360 -> if (isPast) "${TimeUnits.DAY.plural((absDiff / DAY).toInt())} назад"
        else "через ${TimeUnits.DAY.plural((absDiff / DAY).toInt())}"
        else -> if (isPast) "более года назад" else "более чем через год"
    }
}

enum class TimeUnits {
    SECOND, MINUTE, HOUR, DAY;

    fun plural(value: Int): String {
        val plurals = mapOf(
            SECOND to Triple("секунды", "секунду", "секунд"),
            MINUTE to Triple("минуты", "минуту", "минут"),
            HOUR to Triple("часа", "час", "часов"),
            DAY to Triple("дня", "день", "дней")
        )

        val remainder = value % 10
        var quotient = value / 10
        while (quotient > 100) quotient /= 10
        quotient %= 10

        return when {
            (remainder in 2..4) && (quotient != 1) -> "$value ${plurals[this]?.first}"
            (remainder == 1) && (quotient != 1) -> "$value ${plurals[this]?.second}"
            else -> "$value ${plurals[this]?.third}"
        }
    }
}