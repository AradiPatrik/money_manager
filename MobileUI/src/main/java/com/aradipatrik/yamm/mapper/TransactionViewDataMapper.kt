package com.aradipatrik.yamm.mapper

import com.aradipatrik.presentation.presentations.TransactionPresentation
import com.aradipatrik.yamm.model.TransactionViewData

class TransactionViewDataMapper(private val iconMapper: IconMapper) {
    fun mapToViewData(p: TransactionPresentation) = TransactionViewData(
        p.id, p.category.id, p.memo, iconMapper.mapToResource(p.category.iconId),
        p.amount, 0, p.category.name
    )
}
