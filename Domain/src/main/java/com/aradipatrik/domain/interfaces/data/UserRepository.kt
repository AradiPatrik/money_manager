package com.aradipatrik.domain.interfaces.data

import com.aradipatrik.domain.model.User
import io.reactivex.Completable
import io.reactivex.Single

interface UserRepository {
    fun setSignedInUser(user: User): Completable
    fun getSignedInUser(): Single<User>
    fun isUserSignedIn(): Single<Boolean>
}
