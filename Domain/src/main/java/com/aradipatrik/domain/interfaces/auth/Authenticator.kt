package com.aradipatrik.domain.interfaces.auth

import com.aradipatrik.domain.model.User
import com.aradipatrik.domain.model.UserCredentials
import io.reactivex.Single

interface Authenticator {
    fun registerUserWithCredentials(userCredentials: UserCredentials): Single<User>
    fun loginUserWithCredentials(userCredentials: UserCredentials): Single<User>
}
