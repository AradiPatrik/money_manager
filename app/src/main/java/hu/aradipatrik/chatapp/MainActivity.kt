package hu.aradipatrik.chatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import hu.aradipatrik.chatapp.databinding.ActivityMainBinding
import hu.aradipatrik.chatapp.di.viewModel

class MainActivity : AppCompatActivity() {

    private val viewmodel by viewModel { injector.mainViewModel }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel.runBusiness()
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.welcomeText = "Databinding Works!"
    }
}
