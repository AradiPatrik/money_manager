package com.aradipatrik.domain.exceptions.auth

sealed class ValidationException : Exception()
data class PasswordTooShort(val min: Int, val actual: Int) : ValidationException()
