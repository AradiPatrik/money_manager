package com.aradipatrik.domain


import com.aradipatrik.domain.interactor.onboard.IsUserSignedInInteractor
import com.aradipatrik.domain.interactor.onboard.SignUpWithEmailAndPasswordInteractor
import com.aradipatrik.domain.interactor.category.GetCategoriesInteractor
import com.aradipatrik.domain.interactor.onboard.InitializeUserInteractor
import com.aradipatrik.domain.interactor.onboard.LogInWithEmailAndPasswordInteractor
import com.aradipatrik.domain.interactor.selectedmonth.DecrementSelectedMonthInteractor
import com.aradipatrik.domain.interactor.selectedmonth.GetSelectedMonthInteractor
import com.aradipatrik.domain.interactor.selectedmonth.IncrementSelectedMonthInteractor
import com.aradipatrik.domain.interactor.stats.GetAllTimeExpenseStatsInteractor
import com.aradipatrik.domain.interactor.stats.GetMonthlyExpenseStatsInteractor
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
        SignUpWithEmailAndPasswordInteractor(get(), get(), get())
    }
    single {
        SelectFirstWalletInteractor(get())
    }
    single {
        IsUserSignedInInteractor(get())
    }
    single {
        InitializeUserInteractor(get(), get())
    }
    single {
        LogInWithEmailAndPasswordInteractor(get(), get(), get(), get())
    }
    single {
        IncrementSelectedMonthInteractor(get())
    }
    single {
        DecrementSelectedMonthInteractor(get())
    }
    single {
        GetAllTimeExpenseStatsInteractor(get(), get())
    }
    single {
        GetSelectedMonthInteractor(get())
    }
    single {
        GetMonthlyExpenseStatsInteractor(get(), get())
    }
}
