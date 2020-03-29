package com.aradipatrik.data.repository

import com.aradipatrik.data.mapper.CategoryMapper
import com.aradipatrik.data.datastore.category.LocalCategoryDatastore
import com.aradipatrik.domain.model.Category
import com.aradipatrik.domain.interfaces.data.CategoryRepository
import io.reactivex.Completable

class CategoryRepositoryImpl(
    private val syncer: Syncer,
    private val mapper: CategoryMapper,
    private val localDatastore: LocalCategoryDatastore
) : CategoryRepository {
    override fun getAll() = synchronise().andThen(
        localDatastore.getAll()
            .map { it.map(mapper::mapFromEntity) }
    )

    override fun add(category: Category): Completable =
        localDatastore.add(mapper.mapToEntity(category))
            .andThen(synchronise())

    override fun update(category: Category): Completable =
        localDatastore.update(mapper.mapToEntity(category))
            .andThen(synchronise())

    override fun delete(id: String): Completable =
        localDatastore.delete(id).andThen(synchronise())

    private fun synchronise(): Completable = syncer.syncAll()
}
