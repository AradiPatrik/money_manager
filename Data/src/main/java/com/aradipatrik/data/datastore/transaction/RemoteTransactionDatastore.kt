package com.aradipatrik.data.datastore.transaction

import com.aradipatrik.data.model.TransactionPartialEntity
import com.aradipatrik.data.common.RemoteTimestampedDatastore

interface RemoteTransactionDatastore : RemoteTimestampedDatastore<TransactionPartialEntity>
