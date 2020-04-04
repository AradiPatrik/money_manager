package com.aradipatrik.presentation.mapper

import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.presentation.presentations.TransactionPresentation

class TransactionPresentationMapper(
    private val categoryPresentationMapper: CategoryPresentationMapper
) {
    fun mapToPresentation(transaction: Transaction) =
        TransactionPresentation(
            id = transaction.id,
            amount = transaction.amount,
            category = categoryPresentationMapper.mapToPresentation(transaction.category),
            memo = transaction.memo,
            date = transaction.date
        )

    fun mapFromPresentation(presentation: TransactionPresentation) = Transaction(
        id = presentation.id,
        category = categoryPresentationMapper.mapFromPresentation(presentation.category),
        date = presentation.date,
        memo = presentation.memo,
        amount = presentation.amount
    )
}
