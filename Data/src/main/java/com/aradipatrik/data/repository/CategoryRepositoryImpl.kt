package com.aradipatrik.data.repository

import com.aradipatrik.data.mapper.CategoryMapper
import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.data.repository.category.LocalCategoryDataStore
import com.aradipatrik.data.repository.category.RemoteCategoryDataStore
import com.aradipatrik.domain.model.Category
import com.aradipatrik.domain.repository.CategoryRepository
import io.reactivex.Completable
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val syncer: Syncer<CategoryEntity>,
    private val mapper: CategoryMapper,
    private val localDataStore: LocalCategoryDataStore,
    private val remoteDataStore: RemoteCategoryDataStore
) : CategoryRepository {
    override fun getAll() = synchronise().andThen(
        localDataStore.getAll()
            .map { it.map(mapper::mapFromEntity) }
    )

    override fun add(category: Category): Completable =
        localDataStore.add(mapper.mapToEntity(category))
            .andThen(synchronise())

    override fun update(category: Category): Completable =
        localDataStore.update(mapper.mapToEntity(category))

    override fun delete(id: String): Completable =
        localDataStore.delete(id)

    private fun synchronise(): Completable = syncer.sync(localDataStore, remoteDataStore)
}
