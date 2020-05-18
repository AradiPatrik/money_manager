package com.aradipatrik.yamm.common.holder

import androidx.appcompat.widget.Toolbar
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class ToolbarHolder {
    private val toolbarSubject = BehaviorSubject.create<Toolbar>()
    private val titleSubject = BehaviorSubject.create<String>()

    init {
        Observable.combineLatest(
            toolbarSubject,
            titleSubject,
            BiFunction { toolbar: Toolbar, title: String -> toolbar to title }
        )
            .doOnNext { (toolbar, title) -> toolbar.title = title }
            .subscribe()
    }

    fun setToolbar(toolbar: Toolbar) {
        toolbarSubject.onNext(toolbar)
    }

    fun setTitle(title: CharSequence) {
        titleSubject.onNext(title.toString())
    }
}