package com.aradipatrik.domain.usecase

import io.reactivex.Observable

interface ObservableUseCase<T, in Params> {
    fun get(params: Params? = null): Observable<T>
}
