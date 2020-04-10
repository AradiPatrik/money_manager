package com.aradipatrik.data.datastore.transaction

import com.aradipatrik.data.common.RemoteTimestampedDatastore
import com.aradipatrik.data.model.TransactionWithIds

interface RemoteTransactionDatastore : RemoteTimestampedDatastore<TransactionWithIds>
