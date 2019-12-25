package com.aradipatrik.data.repository.common

import io.reactivex.Completable
import io.reactivex.Observable

interface CrudDataStore<E, Id> {
    fun getAll(): Observable<List<E>>
    fun add(item: E): Completable
    fun update(item: E): Completable
    fun delete(id: Id): Completable
}
