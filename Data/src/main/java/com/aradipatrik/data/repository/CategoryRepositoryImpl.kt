package com.aradipatrik.data.repository

import com.aradipatrik.data.datastore.category.LocalCategoryDatastore
import com.aradipatrik.data.mapper.CategoryMapper
import com.aradipatrik.domain.interfaces.data.CategoryRepository
import com.aradipatrik.domain.model.Category
import io.reactivex.Completable

class CategoryRepositoryImpl(
    private val syncer: Syncer,
    private val mapper: CategoryMapper,
    private val localDatastore: LocalCategoryDatastore
) : CategoryRepository {
    override fun getAll(walletId: String) = synchronise().andThen(
        localDatastore.getCategoriesInWallet(walletId)
            .map { it.map(mapper::mapFromEntity) }
    )

    override fun addAll(categories: List<Category>, walletId: String) =
        Completable.merge(
            categories
                .map { mapper.mapToEntity(it).copy(walletId = walletId) }
                .map(localDatastore::add)
        ).andThen(synchronise())

    override fun add(category: Category, walletId: String) =
        localDatastore.add(mapper.mapToEntity(category).copy(walletId = walletId))
            .andThen(synchronise())

    override fun update(category: Category, walletId: String): Completable =
        localDatastore.update(mapper.mapToEntity(category).copy(walletId = walletId))
            .andThen(synchronise())

    override fun delete(id: String): Completable =
        localDatastore.delete(id).andThen(synchronise())

    private fun synchronise(): Completable = syncer.syncAll()
}
