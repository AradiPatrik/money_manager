package com.aradipatrik.data.repository.category

import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.data.repository.common.CrudDataStore
import com.aradipatrik.data.repository.common.LocalTimestampedDataStore

interface LocalCategoryDataStore :
    LocalTimestampedDataStore<CategoryEntity>,
    CrudDataStore<CategoryEntity, String>
