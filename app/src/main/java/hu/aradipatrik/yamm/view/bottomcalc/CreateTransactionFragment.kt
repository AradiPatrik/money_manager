package hu.aradipatrik.yamm.view.bottomcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hu.aradipatrik.yamm.databinding.CalculatorSheetBinding

class CreateTransactionFragment(
        onAdd: (
                categoryName: String,
                memo: String,
                amount: Int
        ) -> Unit
) : BottomSheetDialogFragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = CalculatorSheetBinding.inflate(inflater, container, false)
        return binding.root
    }
}

fun FragmentActivity.showCreateTransactionBottomSheet(
        onAdd: (
                categoryName: String,
                memo: String,
                amount: Int
        ) -> Unit
) {
    CreateTransactionFragment(onAdd).show(
            supportFragmentManager,
            CreateTransactionFragment::class.java.simpleName
    )
}
