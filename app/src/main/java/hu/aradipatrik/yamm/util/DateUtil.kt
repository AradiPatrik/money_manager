package hu.aradipatrik.yamm.util

import org.joda.time.DateTime

fun DateTime.isSameDay(other: DateTime) =
        dayOfYear == other.dayOfYear && year == other.year
