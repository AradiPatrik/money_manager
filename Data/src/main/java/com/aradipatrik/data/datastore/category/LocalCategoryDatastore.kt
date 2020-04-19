package com.aradipatrik.data.datastore.category

import com.aradipatrik.data.common.CrudDatastore
import com.aradipatrik.data.common.LocalTimestampedDatastore
import com.aradipatrik.data.model.CategoryDataModel
import io.reactivex.Observable

interface LocalCategoryDatastore :
    LocalTimestampedDatastore<CategoryDataModel>,
    CrudDatastore<CategoryDataModel, String> {
    fun getCategoriesInWallet(walletId: String): Observable<List<CategoryDataModel>>
}
