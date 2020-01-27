package com.aradipatrik.presentation

import com.aradipatrik.presentation.mapper.CategoryPresentationMapper
import com.aradipatrik.presentation.mapper.TransactionPresentationMapper
import org.koin.dsl.module

val presentationModule = module {
    single { CategoryPresentationMapper() }
    single { TransactionPresentationMapper(get()) }
}