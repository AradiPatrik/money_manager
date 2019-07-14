package hu.aradipatrik.chatapp

import android.util.Log
import javax.inject.Inject

class BusinessUtil @Inject constructor() {
    fun doBusiness() {
        Log.d(this::class.java.simpleName, "Doing business")
    }
}