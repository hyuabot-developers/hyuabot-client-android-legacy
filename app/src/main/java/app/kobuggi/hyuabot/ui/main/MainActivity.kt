package app.kobuggi.hyuabot.ui.main

import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import app.kobuggi.hyuabot.GlobalActivity
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : GlobalActivity() {
    private val vm by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.vm = vm

        val navigationHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navigationHostFragment.navController

        binding.bottomNavigationMenu.setupWithNavController(navController)
        binding.bottomNavigationMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.fragment_menu -> {
                    if (binding.additionalMenuCard.visibility == android.view.View.VISIBLE){
                        binding.additionalMenuCard.visibility = android.view.View.GONE
                    } else {
                        binding.additionalMenuCard.visibility = android.view.View.VISIBLE
                    }
                    return@setOnItemSelectedListener true
                }
                else -> {
                    navController.navigate(it.itemId)
                }
            }
            return@setOnItemSelectedListener false
        }
        binding.fragmentContainer.setOnClickListener {
            if (binding.additionalMenuCard.visibility == android.view.View.VISIBLE){
                binding.additionalMenuCard.visibility = android.view.View.GONE
            }
        }
    }
}