package com.aradipatrik.data.datastore.category

import com.aradipatrik.data.common.CrudDatastore
import com.aradipatrik.data.common.LocalTimestampedDatastore
import com.aradipatrik.data.model.CategoryEntity

interface LocalCategoryDatastore :
    LocalTimestampedDatastore<CategoryEntity>,
    CrudDatastore<CategoryEntity, String>
