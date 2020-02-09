package com.aradipatrik.yamm

import com.aradipatrik.yamm.common.mapper.IconMapper
import com.aradipatrik.yamm.features.add.transaction.adapter.CategoryAdapter
import com.aradipatrik.yamm.features.add.transaction.mapper.CalculatorViewDataMapper
import com.aradipatrik.yamm.features.history.adapter.HistoryAdapter
import com.aradipatrik.yamm.features.history.mapper.TransactionViewDataMapper
import org.koin.dsl.module

val mobileUiModule = module {
    factory { HistoryAdapter() }
    factory { CategoryAdapter() }
    single { TransactionViewDataMapper(get()) }
    single { IconMapper() }
    single { CalculatorViewDataMapper() }
}
