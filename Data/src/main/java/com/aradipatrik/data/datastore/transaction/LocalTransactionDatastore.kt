package com.aradipatrik.data.datastore.transaction

import com.aradipatrik.data.common.CrudDatastore
import com.aradipatrik.data.common.LocalTimestampedDatastore
import com.aradipatrik.data.model.TransactionWithCategory
import com.aradipatrik.data.model.TransactionWithIds
import io.reactivex.Observable
import org.joda.time.Interval

interface LocalTransactionDatastore :
    LocalTimestampedDatastore<TransactionWithIds>,
    CrudDatastore<TransactionWithIds, String> {
    fun getInInterval(interval: Interval): Observable<List<TransactionWithCategory>>
}
