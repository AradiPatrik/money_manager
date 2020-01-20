package com.aradipatrik.yamm.features.history.mapper

import com.aradipatrik.presentation.presentations.TransactionPresentation
import com.aradipatrik.yamm.features.history.model.TransactionItemViewData

class TransactionViewDataMapper(private val iconMapper: IconMapper) {
    fun mapToItemViewData(p: TransactionPresentation) = TransactionItemViewData(
        p.memo, iconMapper.mapToResource(p.category.iconId),
        p.amount, 0, p.category.name, p
    )


}
