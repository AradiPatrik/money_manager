package com.aradipatrik.local.database

import com.aradipatrik.data.datastore.user.LocalUserDatastore
import com.aradipatrik.domain.model.User
import com.f2prateek.rx.preferences2.RxSharedPreferences
import io.reactivex.Completable
import io.reactivex.Single

class RxPreferencesUserDatastore(val rxPreferences: RxSharedPreferences) : LocalUserDatastore {
    companion object {
        const val USER_ID_KEY = "USER_ID"
    }

    override fun setUser(user: User): Completable = Completable.create {
        rxPreferences.getString(USER_ID_KEY).set(user.id)
        it.onComplete()
    }

    override fun getUser(): Single<User> =
        rxPreferences.getString(USER_ID_KEY)
            .asObservable()
            .firstOrError()
            .map { id -> User(id) }
}