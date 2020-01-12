package com.aradipatrik.remote.payloadfactory

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.remote.CATEGORY_NAME_KEY
import com.aradipatrik.remote.DELETED_KEY
import com.aradipatrik.remote.ICON_ID_KEY
import com.aradipatrik.remote.UPDATED_TIMESTAMP_KEY
import com.google.firebase.Timestamp

class CategoryPayloadFactory {
    fun createPayloadFrom(category: CategoryEntity): HashMap<String, Any> = hashMapOf(
        CATEGORY_NAME_KEY to category.name,
        ICON_ID_KEY to category.iconId,
        UPDATED_TIMESTAMP_KEY to Timestamp.now(),
        DELETED_KEY to (category.syncStatus == SyncStatus.ToDelete)
    )
}