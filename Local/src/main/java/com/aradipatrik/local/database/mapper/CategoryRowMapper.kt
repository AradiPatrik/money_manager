package com.aradipatrik.local.database.mapper

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.CategoryDataModel
import com.aradipatrik.local.database.model.category.CategoryRow

class CategoryRowMapper {
    fun mapToRow(e: CategoryDataModel) = CategoryRow(
        uid = e.id,
        name = e.name,
        iconId = e.iconId,
        walletId = e.walletId,
        updateTimestamp = e.updatedTimeStamp,
        syncStatusCode = e.syncStatus.code
    )

    fun mapToEntity(r: CategoryRow) = CategoryDataModel(
        id = r.uid,
        iconId = r.iconId,
        walletId = r.walletId,
        syncStatus = SyncStatus.fromCode(r.syncStatusCode),
        updatedTimeStamp = r.updateTimestamp,
        name = r.name
    )
}
