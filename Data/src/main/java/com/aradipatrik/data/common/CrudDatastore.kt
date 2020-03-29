package com.aradipatrik.data.common

import io.reactivex.Completable
import io.reactivex.Observable

interface CrudDatastore<E, Id> {
    fun getAll(): Observable<List<E>>
    fun add(item: E): Completable
    fun update(item: E): Completable
    fun delete(id: Id): Completable
}
