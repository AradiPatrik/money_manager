package com.aradipatrik.presentation.mapper

import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.presentation.presentations.TransactionPresentationModel

class TransactionPresentationMapper(
    private val categoryPresentationMapper: CategoryPresentationMapper
) {
    fun mapToPresentation(transaction: Transaction) =
        TransactionPresentationModel(
            id = transaction.id,
            amount = transaction.amount,
            categoryModel = categoryPresentationMapper.mapToPresentation(transaction.category),
            memo = transaction.memo,
            date = transaction.date
        )

    fun mapFromPresentation(presentationModel: TransactionPresentationModel) = Transaction(
        id = presentationModel.id,
        category = categoryPresentationMapper.mapFromPresentation(presentationModel.categoryModel),
        date = presentationModel.date,
        memo = presentationModel.memo,
        amount = presentationModel.amount
    )
}
