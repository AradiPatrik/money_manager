package com.aradipatrik.data.mapper

import com.aradipatrik.data.model.CategoryDataModel
import com.aradipatrik.domain.model.Category

class CategoryMapper {
    fun mapFromEntity(dataModel: CategoryDataModel) =
        Category(dataModel.id, dataModel.name, dataModel.iconId)

    fun mapToEntity(domain: Category) = CategoryDataModel(
        id = domain.id, name = domain.name, iconId = domain.iconId, walletId = "",
        updatedTimeStamp = TimestampProvider.now(), syncStatus = SyncStatus.None
    )
}
