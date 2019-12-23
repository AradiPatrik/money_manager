package com.aradipatrik.domain.interactor

import com.aradipatrik.domain.executor.PostExecutionThread
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

abstract class CompletableUseCase<Params> @Inject constructor(
    private val postExecutionThread: PostExecutionThread
) {
    private val disposable = CompositeDisposable()

    internal abstract fun buildUseCaseCompletable(params: Params? = null): Completable

    open fun execute(observer: DisposableCompletableObserver, params: Params? = null) {
        disposable += buildUseCaseCompletable(params)
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
            .subscribeWith(observer)
    }

    fun dispose() = disposable.dispose()
}