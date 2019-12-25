package com.aradipatrik.domain.repository

import com.aradipatrik.domain.model.Category
import com.aradipatrik.domain.model.Transaction
import io.reactivex.Completable
import io.reactivex.Observable

interface CategoryRepository {
    fun getAll(): Observable<List<Category>>
    fun add(category: Category): Completable
    fun update(category: Category): Completable
    fun delete(id: String): Completable
}