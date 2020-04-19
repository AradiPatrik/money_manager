package com.aradipatrik.data.repository

import com.aradipatrik.data.datastore.transaction.LocalTransactionDatastore
import com.aradipatrik.data.mapper.JoinedTransactionMapper
import com.aradipatrik.data.mapper.PartialTransactionMapper
import com.aradipatrik.domain.interfaces.data.TransactionRepository
import com.aradipatrik.domain.interfaces.data.WalletRepository
import com.aradipatrik.domain.model.Transaction
import io.reactivex.Completable
import org.joda.time.Interval

class TransactionRepositoryImpl(
    private val syncer: Syncer,
    private val walletRepository: WalletRepository,
    private val partialTransactionMapper: PartialTransactionMapper,
    private val joinedTransactionMapper: JoinedTransactionMapper,
    private val localTransactionDatastore: LocalTransactionDatastore
) : TransactionRepository {
    override fun getInInterval(interval: Interval) = syncer.syncAll().andThen(
        walletRepository.getSelectedWallet()
            .flatMapObservable { selectedWallet ->
                localTransactionDatastore.getInInterval(interval, selectedWallet.id)
                    .map { transactions ->
                        transactions.map(joinedTransactionMapper::mapFromEntity)
                    }
            }
    )

    override fun add(transaction: Transaction): Completable =
        localTransactionDatastore.add(
            partialTransactionMapper.mapToEntity(transaction)
        ).andThen(syncer.syncAll())

    override fun update(transaction: Transaction): Completable =
        localTransactionDatastore.update(
            partialTransactionMapper.mapToEntity(transaction)
        ).andThen(syncer.syncAll())

    override fun delete(id: String): Completable =
        localTransactionDatastore.delete(id).andThen(syncer.syncAll())
}
