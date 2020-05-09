package com.aradipatrik.domain


import com.aradipatrik.domain.interactor.auth.IsUserSignedInInteractor
import com.aradipatrik.domain.interactor.auth.SignUpWithEmailAndPasswordInteractor
import com.aradipatrik.domain.interactor.category.GetCategoriesInteractor
import com.aradipatrik.domain.interactor.transaction.AddTransactionInteractor
import com.aradipatrik.domain.interactor.transaction.DeleteTransactionInteractor
import com.aradipatrik.domain.interactor.transaction.GetTransactionsInIntervalInteractor
import com.aradipatrik.domain.interactor.transaction.UpdateTransactionInteractor
import com.aradipatrik.domain.interactor.wallet.SelectFirstWalletInteractor
import org.koin.dsl.module

val domainModule = module {
    single {
        GetTransactionsInIntervalInteractor(get(), get())
    }
    single {
        AddTransactionInteractor(get(), get())
    }
    single {
        UpdateTransactionInteractor(get(), get())
    }
    single {
        DeleteTransactionInteractor(get())
    }
    single {
        GetCategoriesInteractor(get(), get())
    }
    single {
        SignUpWithEmailAndPasswordInteractor(get(), get())
    }
    single {
        SelectFirstWalletInteractor(get())
    }
    single {
        IsUserSignedInInteractor(get())
    }
}
