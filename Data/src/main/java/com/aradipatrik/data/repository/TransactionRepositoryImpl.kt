package com.aradipatrik.data.repository

import com.aradipatrik.data.mapper.JoinedTransactionMapper
import com.aradipatrik.data.mapper.PartialTransactionMapper
import com.aradipatrik.data.repository.transaction.LocalTransactionDataStore
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.domain.repository.TransactionRepository
import io.reactivex.Completable
import io.reactivex.Observable
import org.joda.time.Interval
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val syncer: Syncer,
    private val partialTransactionMapper: PartialTransactionMapper,
    private val joinedTransactionMapper: JoinedTransactionMapper,
    private val localTransactionDataStore: LocalTransactionDataStore
) : TransactionRepository {
    override fun getInInterval(interval: Interval) = syncer.syncAll().andThen(
        localTransactionDataStore.getInInterval(interval)
            .map { transactions -> transactions.map(joinedTransactionMapper::mapFromEntity) }
    )

    override fun add(transaction: Transaction): Completable =
        localTransactionDataStore.add(
            partialTransactionMapper.mapToEntity(transaction)
        ).andThen(syncer.syncAll())

    override fun update(transaction: Transaction): Completable =
        localTransactionDataStore.update(
            partialTransactionMapper.mapToEntity(transaction)
        ).andThen(syncer.syncAll())

    override fun delete(id: String): Completable =
        localTransactionDataStore.delete(id).andThen(syncer.syncAll())
}
