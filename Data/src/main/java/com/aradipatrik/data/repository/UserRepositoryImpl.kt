package com.aradipatrik.data.repository

import com.aradipatrik.data.datastore.user.LocalUserDatastore
import com.aradipatrik.domain.interfaces.data.UserRepository
import com.aradipatrik.domain.model.User
import io.reactivex.Completable
import io.reactivex.Single

class UserRepositoryImpl(private val localUserDatastore: LocalUserDatastore) : UserRepository {
    override fun setSignedInUser(user: User): Completable = localUserDatastore.setUser(user)

    override fun getSignedInUser(): Single<User> = localUserDatastore.getUser()
}
