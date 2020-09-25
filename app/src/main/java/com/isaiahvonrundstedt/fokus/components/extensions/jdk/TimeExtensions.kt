package com.isaiahvonrundstedt.fokus.components.extensions.jdk

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 *   An extension function to print the LocalDate
 *   instance
 *   @return the formatted value of LocalDate
 */
fun LocalDate.print(pattern: String?): String? {
    return format(DateTimeFormatter.ofPattern(pattern))
}

/**
 *  An extension function to print the
 *  LocalTime instance
 *
 *  @return the formatted value of the instance in the specified pattern
 */
fun LocalTime.print(pattern: String?): String? {
    return format(DateTimeFormatter.ofPattern(pattern))
}

/**
 *   An extension function to convert the
 *   LocalTime instance to a ZonedDateTime instance
 *   with the current date
 *
 *   @return the ZonedDateTime instance with the values of the
 *          LocalTime instance
 */
fun LocalTime.toZonedDateTimeToday(): ZonedDateTime? {
    return LocalDate.now().atStartOfDay(ZoneId.systemDefault())
        .with(this)
}

/**
 *   An extension function used to
 *   determine if the ZonedDateTime object
 *   is same as the date today
 *
 *   @return true if the date matches from the current date
 */
fun ZonedDateTime.isToday(): Boolean {
    return LocalDate.now().isEqual(this.toLocalDate())
}

/**
 *   An extension function used to
 *   determine if the ZonedDateTime object
 *   is the same as the next day
 *   @return true if the ZonedDateTime object is the
 *           same as the next day
 */
fun ZonedDateTime.isTomorrow(): Boolean {
    return LocalDate.now().plusDays(1).compareTo(this.toLocalDate()) == 0
}

/**
 *   An extension function used to
 *   determine if the ZonedDateTime object
 *   is the same as the previous day
 *   @return true if the ZonedDateTime object is the
 *           same as the previous day
 */
fun ZonedDateTime.isYesterday(): Boolean {
    return LocalDate.now().minusDays(1).compareTo(this.toLocalDate()) == 0
}

/**
 *   An extension function used to determine
 *   if the ZonedDateTime object is before
 *   the current datetime
 *   @return true if the current ZonedDateTime object
 *              is before the current date-time
 */
fun ZonedDateTime.isBeforeNow(): Boolean {
    return this.isBefore(ZonedDateTime.now())
}

/**
 *   An extension function used to determine
 *   if the ZonedDateTime object is after
 *   the current datetime
 *   @return true if the current ZonedDateTime object
 *           is after the current date-time
 */
fun ZonedDateTime.isAfterNow(): Boolean {
    return this.isAfter(ZonedDateTime.now())
}

/**
 *  An extension function used to convert the
 *  current instance of ZonedDateTime to
 *  a legacy Calendar instance
 *  @return calendar instance with the same values as
 *          this instance
 */
fun ZonedDateTime.toCalendar(): Calendar {
    return GregorianCalendar.from(this)
}

/**
 *  An extension function used to print the
 *  ZonedDateTime instance as String with the
 *  specified pattern
 *  @param pattern the pattern that will be used to
 *                  convert the ZonedDateTime instance to a String
 *  @return the formatted string
 */
fun ZonedDateTime.print(pattern: String?): String? {
    return this.format(DateTimeFormatter.ofPattern(pattern))
}

/**
 *   An extension function used to reset the ZonedDateTime
 *   instance time values to 00:00 or midnight
 *   @return the same ZonedDateTime instance with a different time
 */
fun ZonedDateTime.atWithStartOfDay(): ZonedDateTime? {
    return this.toLocalDate().atStartOfDay(ZoneId.systemDefault())
}