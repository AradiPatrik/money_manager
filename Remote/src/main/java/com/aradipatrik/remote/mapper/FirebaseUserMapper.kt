package com.aradipatrik.remote.mapper

import com.aradipatrik.domain.model.User
import com.google.firebase.auth.FirebaseUser

class FirebaseUserMapper {
    fun mapFrom(user: FirebaseUser): User = User(user.uid)
}
