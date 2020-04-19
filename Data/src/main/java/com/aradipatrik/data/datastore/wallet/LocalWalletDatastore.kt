package com.aradipatrik.data.datastore.wallet

import com.aradipatrik.data.common.CrudDatastore
import com.aradipatrik.data.common.LocalTimestampedDatastore
import com.aradipatrik.data.model.WalletDataModel
import io.reactivex.Completable
import io.reactivex.Single

interface LocalWalletDatastore :
    LocalTimestampedDatastore<WalletDataModel>,
    CrudDatastore<WalletDataModel, String> {
    fun setSelected(walletId: String): Completable
    fun getSelected(): Single<WalletDataModel>
}
