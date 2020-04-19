package com.aradipatrik.yamm.features.history.mapper

import com.aradipatrik.presentation.presentations.TransactionPresentationModel
import com.aradipatrik.yamm.common.mapper.IconMapper
import com.aradipatrik.yamm.features.history.model.TransactionHeaderViewData
import com.aradipatrik.yamm.features.history.model.TransactionItemViewData
import org.joda.time.LocalDate

class TransactionViewDataMapper(private val iconMapper: IconMapper) {
    fun mapToItemViewData(p: TransactionPresentationModel) = TransactionItemViewData(
        p.memo, iconMapper.mapToResource(p.categoryModel.iconId),
        p.amount, 0, p.categoryModel.name, p
    )

    fun mapToHeaderViewData(localDate: LocalDate) = TransactionHeaderViewData(localDate)
}
