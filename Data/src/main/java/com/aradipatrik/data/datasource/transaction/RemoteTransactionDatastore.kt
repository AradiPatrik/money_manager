package com.aradipatrik.data.datasource.transaction

import com.aradipatrik.data.model.TransactionPartialEntity
import com.aradipatrik.data.common.RemoteTimestampedDataStore

interface RemoteTransactionDatastore
    : RemoteTimestampedDataStore<TransactionPartialEntity>
