package hu.aradipatrik.chatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import hu.aradipatrik.chatapp.databinding.ActivityMainBinding
import hu.aradipatrik.chatapp.di.viewModel

class MainActivity : AppCompatActivity() {

    private val viewmodel by viewModel { injector.mainViewModel }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val navController = findNavController(R.id.nav_host_fragment)
        binding.toolbar.setupWithNavController(
            navController,
            AppBarConfiguration(setOf(R.id.historyFragment, R.id.diagramsFragment))
        )
        viewmodel.foo()
        binding.bottomNav.setupWithNavController(navController)
    }
}
