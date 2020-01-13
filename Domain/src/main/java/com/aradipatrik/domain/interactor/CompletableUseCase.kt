package com.aradipatrik.domain.interactor

import io.reactivex.Completable

interface CompletableUseCase<Params> {
    fun get(params: Params? = null): Completable
}
