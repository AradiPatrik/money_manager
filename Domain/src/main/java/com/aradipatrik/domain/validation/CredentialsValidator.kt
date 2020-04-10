package com.aradipatrik.domain.validation

import com.aradipatrik.domain.MIN_PASSWORD_LENGTH
import com.aradipatrik.domain.exceptions.auth.PasswordTooShort
import com.aradipatrik.domain.model.UserCredentials

object CredentialsValidator {
    fun validate(credentials: UserCredentials) {
        if (credentials.password.length < MIN_PASSWORD_LENGTH) {
            throw PasswordTooShort(min = MIN_PASSWORD_LENGTH, actual = credentials.password.length)
        }
    }
}
