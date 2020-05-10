package com.aradipatrik.domain.interfaces.data

import com.aradipatrik.domain.model.Category
import io.reactivex.Completable
import io.reactivex.Observable

interface CategoryRepository {
    fun getAll(walletId: String): Observable<List<Category>>
    fun addAll(categories: List<Category>, walletId: String): Completable
    fun add(category: Category, walletId: String): Completable
    fun update(category: Category, walletId: String): Completable
    fun delete(id: String): Completable
}
