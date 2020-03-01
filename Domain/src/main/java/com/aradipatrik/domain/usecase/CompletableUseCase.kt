package com.aradipatrik.domain.usecase

import io.reactivex.Completable

interface CompletableUseCase<Params> {
    fun get(params: Params? = null): Completable
}
