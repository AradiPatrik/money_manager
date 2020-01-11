package com.aradipatrik.local.database

import com.aradipatrik.data.datasource.category.LocalCategoryDataStore
import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.local.database.category.CategoryDao
import com.aradipatrik.local.database.mapper.CategoryRowMapper
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class RoomLocalCategoryDataSource(
    private val categoryDao: CategoryDao,
    private val categoryMapper: CategoryRowMapper
) : LocalCategoryDataStore {
    override fun updateWith(elements: List<CategoryEntity>): Completable =
        categoryDao.insert(elements.map(categoryMapper::mapToRow))

    override fun getPending(): Single<List<CategoryEntity>> =
        categoryDao.getPendingCategories().map { rows ->
            rows.map(categoryMapper::mapToEntity)
        }

    override fun clearPending(): Completable = categoryDao.clearPending()

    override fun getLastSyncTime(): Single<Long> = categoryDao.getLastSyncTime()

    override fun getAll(): Observable<List<CategoryEntity>> =
        categoryDao.getAllCategories().map { rows ->
            rows.map(categoryMapper::mapToEntity)
        }

    override fun add(item: CategoryEntity): Completable =
        categoryDao.insert(listOf(categoryMapper.mapToRow(item)))

    override fun update(item: CategoryEntity): Completable = add(item)

    override fun delete(id: String): Completable =
        categoryDao.setDeleted(id)
}