package com.aradipatrik.domain.exceptions.auth

import com.aradipatrik.domain.model.UserCredentials

sealed class ValidationException : Exception()
data class PasswordTooShort(val min: Int, val actual: Int) : ValidationException()

sealed class ServerException : Exception()
data class InvalidCredentialsException(val userCredentials: UserCredentials) : ServerException()
data class UserAlreadyExistsException(val userCredentials: UserCredentials) : ServerException()
