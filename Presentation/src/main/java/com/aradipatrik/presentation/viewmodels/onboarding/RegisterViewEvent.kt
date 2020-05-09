package com.aradipatrik.presentation.viewmodels.onboarding

sealed class RegisterViewEvent {
    data class EmailChange(val newValue: String): RegisterViewEvent()
    data class PasswordChange(val newValue: String): RegisterViewEvent()
    object RegisterClick: RegisterViewEvent()
}
