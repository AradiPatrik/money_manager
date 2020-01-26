package com.aradipatrik.presentation.common

val Int.withLastDigitRemoved get() = div(10)

fun Int.appendDigit(digit: Int) = "$this$digit".toInt()
