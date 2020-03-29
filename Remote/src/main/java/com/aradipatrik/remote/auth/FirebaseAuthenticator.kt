package com.aradipatrik.remote.auth

import com.aradipatrik.domain.interfaces.auth.Authenticator
import com.aradipatrik.domain.model.User
import com.aradipatrik.domain.model.UserCredentials
import com.aradipatrik.remote.mapper.FirebaseErrorMapper
import com.aradipatrik.remote.mapper.FirebaseUserMapper
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class FirebaseAuthenticator(
    private val firebaseUserMapper: FirebaseUserMapper,
    private val firebaseErrorMapper: FirebaseErrorMapper
) : Authenticator {
    companion object {
        const val PASSWORD_MIN_SIZE = 6
    }

    override fun registerUserWithCredentials(userCredentials: UserCredentials) =
        Single.create<User> { emitter ->
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(userCredentials.email, userCredentials.password)
                .addOnSuccessListener { result ->
                    result.user!!.let(firebaseUserMapper::mapFrom).let(emitter::onSuccess)
                }
                .addOnFailureListener {
                    emitter.onError(firebaseErrorMapper.mapFrom(it, userCredentials))
                }
                .addOnCanceledListener { emitter.onError(Exception("Registration cancelled")) }
        }.observeOn(Schedulers.io())
}