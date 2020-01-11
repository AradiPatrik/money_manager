package com.aradipatrik.local.database.mapper

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.local.database.category.CategoryRow

class CategoryRowMapper {
    fun mapToRow(e: CategoryEntity) = CategoryRow(
        uid = e.id,
        name = e.name,
        iconId = e.iconId,
        updateTimestamp = e.updatedTimeStamp,
        syncStatusCode = e.syncStatus.code
    )

    fun mapToEntity(r: CategoryRow) = CategoryEntity(
        id = r.uid,
        iconId = r.iconId,
        syncStatus = SyncStatus.fromCode(r.syncStatusCode),
        updatedTimeStamp = r.updateTimestamp,
        name = r.name
    )
}
