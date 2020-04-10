package com.aradipatrik.data.datastore.user

import com.aradipatrik.domain.model.User
import io.reactivex.Completable
import io.reactivex.Single

interface LocalUserDatastore {
    fun setUser(user: User): Completable
    fun getUser(): Single<User>
}
