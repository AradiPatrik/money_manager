package com.aradipatrik.yamm

import com.aradipatrik.yamm.common.mapper.IconMapper
import com.aradipatrik.yamm.features.addtransaction.view.CategoryAdapter
import com.aradipatrik.yamm.features.addtransaction.mapper.CalculatorViewDataMapper
import com.aradipatrik.yamm.features.addtransaction.mapper.CategoryItemViewDataMapper
import com.aradipatrik.yamm.features.history.view.HistoryAdapter
import com.aradipatrik.yamm.features.history.mapper.TransactionViewDataMapper
import org.koin.dsl.module

val mobileUiModule = module {
    factory { HistoryAdapter() }
    factory { CategoryAdapter() }
    single { TransactionViewDataMapper(get()) }
    single { IconMapper() }
    single { CalculatorViewDataMapper() }
    single { CategoryItemViewDataMapper(get()) }
}
