package com.aradipatrik.data.repository

import com.aradipatrik.data.mapper.TransactionMapper
import com.aradipatrik.data.repository.transaction.LocalTransactionDataStore
import com.aradipatrik.data.repository.transaction.RemoteTransactionRemoteDataStore
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.domain.repository.TransactionRepository
import io.reactivex.Completable
import org.joda.time.Interval
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val localTransactionDataStore: LocalTransactionDataStore,
    private val remoteTransactionDataStore: RemoteTransactionRemoteDataStore,
    private val transactionMapper: TransactionMapper
) : TransactionRepository {
    override fun getAll() = synchronise().andThen(
        localTransactionDataStore.getAll()
            .map { transactions -> transactions.map(transactionMapper::mapFromEntity) }
    )

    override fun getInInterval(interval: Interval) = synchronise().andThen(
        localTransactionDataStore.getInInterval(interval)
            .map { transactions -> transactions.map(transactionMapper::mapFromEntity) }
    )

    override fun add(transaction: Transaction): Completable =
        localTransactionDataStore.add(
            transactionMapper.mapToEntity(transaction)
        ).andThen(synchronise())

    override fun update(transaction: Transaction): Completable =
        localTransactionDataStore.update(
            transactionMapper.mapToEntity(transaction)
        ).andThen(synchronise())

    override fun delete(id: String): Completable =
        localTransactionDataStore.delete(id).andThen(synchronise())

    private fun synchronise(): Completable = Syncer.sync(
        localTransactionDataStore, remoteTransactionDataStore
    )
}
