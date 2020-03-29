package com.aradipatrik.data.datastore.category

import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.data.common.CrudDatastore
import com.aradipatrik.data.common.LocalTimestampedDatastore

interface LocalCategoryDatastore :
    LocalTimestampedDatastore<CategoryEntity>,
    CrudDatastore<CategoryEntity, String>
