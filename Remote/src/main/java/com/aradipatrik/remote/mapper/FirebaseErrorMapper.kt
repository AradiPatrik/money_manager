package com.aradipatrik.remote.mapper

import com.aradipatrik.domain.exceptions.auth.InvalidCredentialsException
import com.aradipatrik.domain.exceptions.auth.PasswordTooShort
import com.aradipatrik.domain.exceptions.auth.UserAlreadyExistsException
import com.aradipatrik.domain.model.UserCredentials
import com.aradipatrik.remote.auth.FirebaseAuthenticator
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class FirebaseErrorMapper {
    fun mapFrom(error: Throwable, userCredentials: UserCredentials) = when (error) {
        is FirebaseAuthWeakPasswordException ->
            PasswordTooShort(
                FirebaseAuthenticator.PASSWORD_MIN_SIZE,
                userCredentials.password.length
            )
        is FirebaseAuthUserCollisionException ->
            UserAlreadyExistsException(userCredentials)
        is FirebaseAuthInvalidCredentialsException ->
            InvalidCredentialsException(userCredentials)
        else -> error
    }
}
