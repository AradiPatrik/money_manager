package com.aradipatrik.data.datastore.wallet

import com.aradipatrik.data.common.CrudDatastore
import com.aradipatrik.data.common.LocalTimestampedDatastore
import com.aradipatrik.data.model.WalletEntity

interface LocalWalletDatastore :
    LocalTimestampedDatastore<WalletEntity>,
    CrudDatastore<WalletEntity, String>
