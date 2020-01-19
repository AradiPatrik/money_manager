package com.aradipatrik.local.database

import com.aradipatrik.data.datasource.category.LocalCategoryDatastore
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.local.database.category.CategoryDao
import com.aradipatrik.local.database.mapper.CategoryRowMapper
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

class RoomLocalCategoryDatastore(
    private val categoryDao: CategoryDao,
    private val categoryMapper: CategoryRowMapper
) : LocalCategoryDatastore {
    override fun updateWith(elements: List<CategoryEntity>): Completable =
        categoryDao.insert(elements.map(categoryMapper::mapToRow))

    override fun getPending(): Single<List<CategoryEntity>> =
        categoryDao.getPendingCategories().map { rows ->
            rows.map(categoryMapper::mapToEntity)
        }

    override fun clearPending(): Completable = categoryDao.clearPending()

    override fun getLastSyncTime(): Single<Long> = categoryDao.getLastSyncTime()
        .switchIfEmpty(Maybe.just(0L))
        .toSingle()

    override fun getAll(): Observable<List<CategoryEntity>> =
        categoryDao.getAllCategories().map { rows ->
            rows.map(categoryMapper::mapToEntity)
        }

    override fun add(item: CategoryEntity): Completable =
        categoryDao.insert(
            listOf(categoryMapper.mapToRow(item.copy(syncStatus = SyncStatus.ToAdd)))
        )

    override fun update(item: CategoryEntity): Completable =
        categoryDao.insert(
            listOf(categoryMapper.mapToRow(item.copy(syncStatus = SyncStatus.ToUpdate)))
        )

    override fun delete(id: String): Completable =
        categoryDao.setDeleted(id)
}