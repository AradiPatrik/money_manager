package com.aradipatrik.testing

import org.joda.time.DateTime
import org.joda.time.Interval
import java.util.*
import kotlin.random.Random

@SuppressWarnings("TooManyFunctions") // it is OK because these are just mocks
@Suppress("MemberVisibilityCanBePrivate") // because we want to be able to use them later
object CommonMocks {
    private const val MIN_YEAR = 1900
    private const val MAX_YEAR = 2500
    private const val MIN_MONTH = 1
    private const val MAX_MONTH = 12
    private const val MIN_DAY_OF_MONTH = 0
    private const val MAX_DAY_OF_MONTH = 28
    private const val MIN_HOUR = 0
    private const val MAX_HOUR = 24
    private const val MIN_MINUTE = 0
    private const val MAX_MINUTE = 60
    private const val MIN_SECOND = 0
    private const val MAX_SECOND = 60

    fun string() = UUID.randomUUID().toString()
    fun int() = Random.nextInt()
    fun long() = Random.nextLong()
    fun boolean() = Random.nextBoolean()
    fun year() = Random.nextInt(MIN_YEAR, MAX_YEAR)
    fun month() = Random.nextInt(MIN_MONTH, MAX_MONTH)
    fun day() = Random.nextInt(MIN_DAY_OF_MONTH, MAX_DAY_OF_MONTH)
    fun hour() = Random.nextInt(MIN_HOUR, MAX_HOUR)
    fun minute() = Random.nextInt(MIN_MINUTE, MAX_MINUTE)
    fun second() = Random.nextInt(MIN_SECOND, MAX_SECOND)

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
