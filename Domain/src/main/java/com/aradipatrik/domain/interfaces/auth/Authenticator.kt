package com.aradipatrik.domain.interfaces.auth

import com.aradipatrik.domain.model.User
import com.aradipatrik.domain.model.UserCredentials
import io.reactivex.Completable

interface Authenticator {
    fun registerUserWithCredentials(userCredentials: UserCredentials): Completable
    val currentUser: User?
}
