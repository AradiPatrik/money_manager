package com.aradipatrik.data.datastore.transaction

import com.aradipatrik.data.common.CrudDatastore
import com.aradipatrik.data.common.LocalTimestampedDatastore
import com.aradipatrik.data.model.TransactionWithCategoryDataModel
import com.aradipatrik.data.model.TransactionWithIdsDataModel
import io.reactivex.Observable
import org.joda.time.Interval

interface LocalTransactionDatastore :
    LocalTimestampedDatastore<TransactionWithIdsDataModel>,
    CrudDatastore<TransactionWithIdsDataModel, String> {
    fun getInInterval(
        interval: Interval,
        walletId: String
    ): Observable<List<TransactionWithCategoryDataModel>>
}
