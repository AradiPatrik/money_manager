package com.aradipatrik.yamm.view.bottomnav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.aradipatrik.yamm.databinding.FragmentBottomNavigationBinding

class BottomNavigationFragment(
        private val onMenuItemClick: (MenuItem) -> Unit
) : BottomSheetDialogFragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding =
                FragmentBottomNavigationBinding.inflate(inflater, container, false)
        binding.navigationView.setNavigationItemSelectedListener {
            onMenuItemClick(it)
            dismiss()
            true
        }
        return binding.root
    }
}

fun FragmentActivity.showBottomSheetNavigation(
        onMenuItemClick: (MenuItem) -> Unit
) {
    BottomNavigationFragment(onMenuItemClick).show(
            supportFragmentManager,
            BottomNavigationFragment::class.java.simpleName
    )
}
