package hu.aradipatrik.chatapp.view.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import hu.aradipatrik.chatapp.R
import hu.aradipatrik.chatapp.databinding.ActivityMainBinding
import hu.aradipatrik.chatapp.di.viewModel
import hu.aradipatrik.chatapp.injector
import hu.aradipatrik.chatapp.view.bottomnav.showBottomSheetNavigation
import hu.aradipatrik.chatapp.view.viewext.onStateChange

class MainActivity : AppCompatActivity() {

  private val historyViewModel by viewModel { injector.historyViewModel }

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
    BottomSheetBehavior.from(binding.sumSheetCardView.sumSheetCardView)
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
    setupListeners()
    setupAppFrame()
    setupSumSheet()
    setSupportActionBar(binding.bottomAppBar)
    showHideFabOnSumSheetStateChange()
    hideSumSheet()
  }

  private fun setupAppFrame() {
    binding.toolbarTitle = historyViewModel.selectedMonth
  }

  private fun setupListeners() {
    binding.fab.setOnClickListener {
      binding.bottomAppBar.fabAlignmentMode =
        BottomAppBar.FAB_ALIGNMENT_MODE_END
    }

    binding.sumSheetCardView.rightChevron.setOnClickListener {
      historyViewModel.nextMonth()
    }

    binding.sumSheetCardView.leftChevron.setOnClickListener {
      historyViewModel.previousMonth()
    }
  }

  private fun hideSumSheet() {
    sumSheet.state = BottomSheetBehavior.STATE_HIDDEN
  }

  private fun showHideFabOnSumSheetStateChange() {
    sumSheet.onStateChange {
      if (it == BottomSheetBehavior.STATE_HIDDEN) {
        binding.fab.show()
      } else {
        binding.fab.hide()
      }
    }
  }

  private fun setupSumSheet() {
    binding.sumSheetCardView.month = historyViewModel.selectedMonth
    binding.sumSheetCardView.year = historyViewModel.selectedYear
    binding.sumSheetCardView.monthlyTotal = historyViewModel.monthlySaving
    binding.sumSheetCardView.monthlyExpense = historyViewModel.monthlyExpense
    binding.sumSheetCardView.monthlyIncome = historyViewModel.monthlyIncome
    binding.sumSheetCardView.totalAmount =
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
}
