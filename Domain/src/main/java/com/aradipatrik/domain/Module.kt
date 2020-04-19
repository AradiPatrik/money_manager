package com.aradipatrik.domain


import com.aradipatrik.domain.interactor.auth.SignUpWithEmailAndPasswordInteractor
import com.aradipatrik.domain.interactor.category.GetCategoriesInteractor
import com.aradipatrik.domain.interactor.transaction.AddTransactionInteractor
import com.aradipatrik.domain.interactor.transaction.DeleteTransactionInteractor
import com.aradipatrik.domain.interactor.transaction.GetTransactionsInIntervalInteractor
import com.aradipatrik.domain.interactor.transaction.UpdateTransactionInteractor
import com.aradipatrik.domain.interactor.wallet.SelectFirstWalletInteractor
import org.koin.dsl.module

val domainModule = module {
    factory {
        GetTransactionsInIntervalInteractor(get())
    }
    factory {
        AddTransactionInteractor(get())
    }
    factory {
        UpdateTransactionInteractor(get())
    }
    factory {
        DeleteTransactionInteractor(get())
    }
    factory {
        GetCategoriesInteractor(get())
    }
    factory {
        SignUpWithEmailAndPasswordInteractor(get(), get())
    }
    single {
        SelectFirstWalletInteractor(get())
    }
}
