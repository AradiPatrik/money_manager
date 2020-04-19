package com.aradipatrik.local.database

import com.aradipatrik.data.datastore.category.LocalCategoryDatastore
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.CategoryDataModel
import com.aradipatrik.local.database.model.category.CategoryDao
import com.aradipatrik.local.database.mapper.CategoryRowMapper
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

class RoomLocalCategoryDatastore(
    private val categoryDao: CategoryDao,
    private val categoryMapper: CategoryRowMapper
) : LocalCategoryDatastore {
    override fun getCategoriesInWallet(walletId: String) =
        categoryDao.getCategoriesInsideWallet(walletId)
            .map { rows ->
                rows.map(categoryMapper::mapToEntity)
            }

    override fun updateWith(elements: List<CategoryDataModel>) =
        categoryDao.insert(elements.map(categoryMapper::mapToRow))

    override fun getPending(): Single<List<CategoryDataModel>> =
        categoryDao.getPendingCategories()
            .map { rows ->
                rows.map(categoryMapper::mapToEntity)
            }

    override fun clearPending(): Completable = categoryDao.clearPending()

    override fun getLastSyncTime(): Single<Long> = categoryDao.getLastSyncTime()
        .switchIfEmpty(Maybe.just(0L))
        .toSingle()

    override fun getAll(): Observable<List<CategoryDataModel>> =
        categoryDao.getAllCategories().map { rows ->
            rows.map(categoryMapper::mapToEntity)
        }

    override fun add(item: CategoryDataModel) =
        categoryDao.insert(
            listOf(categoryMapper.mapToRow(item.copy(syncStatus = SyncStatus.ToAdd)))
        )

    override fun update(item: CategoryDataModel) =
        categoryDao.insert(
            listOf(categoryMapper.mapToRow(item.copy(syncStatus = SyncStatus.ToUpdate)))
        )

    override fun delete(id: String) = categoryDao.setDeleted(id)
}
