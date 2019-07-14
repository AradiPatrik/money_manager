package hu.aradipatrik.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import hu.aradipatrik.chatapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val businessRunner = DaggerAppComponent.create().businessRunner
        businessRunner.runBusiness()
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.welcomeText = "Databinding Works!"
    }
}
