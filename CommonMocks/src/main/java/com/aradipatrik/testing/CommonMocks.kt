package com.aradipatrik.testing

import org.joda.time.DateTime
import org.joda.time.Interval
import java.util.*
import kotlin.random.Random

@Suppress("MemberVisibilityCanBePrivate") // because we want to be able to use them later
object CommonMocks {
    fun string() = UUID.randomUUID().toString()
    fun int() = Random.nextInt()
    fun long() = Random.nextLong()
    fun boolean() = Random.nextBoolean()
    fun year() = Random.nextInt(1900, 2500)
    fun month() = Random.nextInt(1, 12)
    fun day() = Random.nextInt(1, 28)
    fun hour() = Random.nextInt(0, 24)
    fun minute() = Random.nextInt(0, 60)
    fun second() = Random.nextInt(0, 60)

    fun date() = DateTime(
        year(),
        month(),
        day(),
        hour(),
        minute(),
        second()
    )

    fun interval(
        startDate: DateTime = DateTime.now(),
        endDate: DateTime = DateTime.now().plusDays(1)
    ) = Interval(startDate, endDate)
}
