package com.aradipatrik.presentation.common

val Int.withLastDigitRemoved get() = div(other = 10)

fun Int.appendDigit(digit: Int) = "$this$digit".toInt()
