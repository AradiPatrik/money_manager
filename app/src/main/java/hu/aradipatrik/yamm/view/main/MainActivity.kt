package hu.aradipatrik.yamm.view.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import hu.aradipatrik.yamm.R
import hu.aradipatrik.yamm.databinding.ActivityMainBinding
import hu.aradipatrik.yamm.di.viewModel
import hu.aradipatrik.yamm.injector
import hu.aradipatrik.yamm.view.bottomnav.showBottomSheetNavigation
import hu.aradipatrik.yamm.view.categoryselect.CategoryListAdapter
import hu.aradipatrik.yamm.view.viewext.onStateChange

class MainActivity : AppCompatActivity() {

    private val historyViewModel by viewModel { injector.historyViewModel }
    private val categorySelectViewModel by viewModel { injector.categorySelectViewModel }

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(
                this,
                R.layout.activity_main
        )
    }

    private val navController by lazy {
        findNavController(R.id.nav_host_fragment)
    }

    private val sumSheet by lazy {
        BottomSheetBehavior.from(binding.sumSheetContainer.sumSheetContainer)
    }

    private val calculatorSheet by lazy {
        BottomSheetBehavior.from(binding.calculatorSheet.calculatorSheet)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbar.setupWithNavController(
                navController,
                AppBarConfiguration(
                        setOf(
                                R.id.historyFragment,
                                R.id.diagramsFragment
                        )
                )
        )
        binding.lifecycleOwner = this
        val adapter = CategoryListAdapter {
            Log.d(this::class.java.simpleName, it)
        }
        binding.calculatorSheet.categorySelectRecyclerView.adapter = adapter
        categorySelectViewModel.allCategories.observe(this) {
            if (it != null) {
                adapter.submitList(it)
            }
        }
        setupListeners()
        setupAppFrame()
        setupSumSheet()
        setSupportActionBar(binding.bottomAppBar)
        showHideFabOnSumSheetStateChange()
        hideSheets()
    }

    private fun setupAppFrame() {
        binding.toolbarTitle = historyViewModel.selectedMonth
    }

    private fun setupListeners() {
        binding.fab.setOnClickListener {
            //      navController.navigate(R.id.action_historyFragment_to_categorySelectFragment)
            showCalculatorBottomSheet()
        }

        binding.sumSheetContainer.rightChevron.setOnClickListener {
            historyViewModel.nextMonth()
        }

        binding.sumSheetContainer.leftChevron.setOnClickListener {
            historyViewModel.previousMonth()
        }
    }

    private fun hideSheets() {
        sumSheet.state = BottomSheetBehavior.STATE_HIDDEN
        calculatorSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun showHideFabOnSumSheetStateChange() {
        sumSheet.onStateChange {
            if (it == BottomSheetBehavior.STATE_HIDDEN) {
                binding.fab.show()
            } else {
                binding.fab.hide()
            }
        }

        calculatorSheet.onStateChange {
            if (it == BottomSheetBehavior.STATE_HIDDEN) {
                binding.fab.show()
            } else {
                binding.fab.hide()
            }
        }
    }

    private fun setupSumSheet() {
        binding.sumSheetContainer.month = historyViewModel.selectedMonth
        binding.sumSheetContainer.year = historyViewModel.selectedYear
        binding.sumSheetContainer.monthlyTotal = historyViewModel.monthlySaving
        binding.sumSheetContainer.monthlyExpense = historyViewModel.monthlyExpense
        binding.sumSheetContainer.monthlyIncome = historyViewModel.monthlyIncome
        binding.sumSheetContainer.totalAmount =
                historyViewModel.totalTillSelectedMonth
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let { menuInflater.inflate(R.menu.menu_history_bottom_appbar, it) }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> showBottomSheetNavigation {
                navController.navigate(it.itemId)
            }
            R.id.wallet -> showWalletBottomSheet()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showWalletBottomSheet() {
        sumSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun showCalculatorBottomSheet() {
        calculatorSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onBackPressed() {
        if (calculatorSheet.state != BottomSheetBehavior.STATE_HIDDEN) {
            calculatorSheet.state = BottomSheetBehavior.STATE_HIDDEN
        }

        if (sumSheet.state != BottomSheetBehavior.STATE_HIDDEN) {
            sumSheet.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }
}
