package hu.aradipatrik.chatapp

import android.util.Log
import javax.inject.Inject

class BusinessRunner @Inject constructor(private val businessUtil: BusinessUtil) {
    fun runBusiness() {

        Log.d(this::class.java.simpleName, "runningBusiness")
        businessUtil.doBusiness()
    }
}