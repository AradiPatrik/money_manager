package com.aradipatrik.domain.interactor

import com.aradipatrik.domain.executor.PostExecutionThread
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

abstract class ObservableUseCase<T, in Params> @Inject constructor(
    private val postExecutionThread: PostExecutionThread
) {
    private val disposable = CompositeDisposable()

    internal abstract fun buildUseCaseObservable(params: Params? = null): Observable<T>

    open fun execute(observer: DisposableObserver<T>, params: Params? = null) {
        disposable += buildUseCaseObservable(params)
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
            .subscribeWith(observer)
    }

    fun dispose() = disposable.dispose()
}