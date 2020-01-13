package com.aradipatrik.domain.interactor

import io.reactivex.Observable

interface ObservableUseCase<T, in Params> {
    fun get(params: Params? = null): Observable<T>
}
