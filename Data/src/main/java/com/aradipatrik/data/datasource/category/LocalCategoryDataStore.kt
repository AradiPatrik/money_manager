package com.aradipatrik.data.datasource.category

import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.data.common.CrudDataStore
import com.aradipatrik.data.common.LocalTimestampedDataStore

interface LocalCategoryDataStore :
    LocalTimestampedDataStore<CategoryEntity>,
    CrudDataStore<CategoryEntity, String>
