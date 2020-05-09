package com.aradipatrik.presentation.common

interface ViewEventProcessor<T> {
    fun processEvent(event: T)
}