package hu.aradipatrik.chatapp.view.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
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

    private val viewmodel by viewModel { injector.mainViewModel }

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        )
    }

    private val navController by lazy {
        findNavController(R.id.nav_host_fragment)
    }

    private val walletSheet by lazy {
        BottomSheetBehavior.from(binding.sumSheet.sumSheet)
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
        binding.bottomAppBar.setOnMenuItemClickListener {
            Log.d(this::class.java.simpleName, "${it.title}")
            true
        }
        binding.fab.setOnClickListener {
            binding.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
        }
        setSupportActionBar(binding.bottomAppBar)
        walletSheet.onStateChange {
            if (it == BottomSheetBehavior.STATE_HIDDEN) {
                binding.fab.show()
            } else {
                binding.fab.hide()
            }
        }
        walletSheet.state = BottomSheetBehavior.STATE_HIDDEN
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
        walletSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }
}
