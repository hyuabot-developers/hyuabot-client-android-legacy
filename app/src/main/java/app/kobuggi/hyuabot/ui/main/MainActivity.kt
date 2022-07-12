package app.kobuggi.hyuabot.ui.main

import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import app.kobuggi.hyuabot.GlobalActivity
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ActivityMainBinding
import app.kobuggi.hyuabot.ui.main.bus.BusFragment
import app.kobuggi.hyuabot.ui.main.cafeteria.CafeteriaFragment
import app.kobuggi.hyuabot.ui.main.shuttle.ShuttleFragment
import app.kobuggi.hyuabot.ui.main.subway.SubwayFragment
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
            if(it.itemId == R.id.fragment_menu) {
                if (binding.additionalMenuCard.visibility == android.view.View.VISIBLE) {
                    binding.additionalMenuCard.visibility = android.view.View.GONE
                } else {
                    binding.additionalMenuCard.visibility = android.view.View.VISIBLE
                }
                return@setOnItemSelectedListener false
            } else {
                binding.additionalMenuCard.visibility = android.view.View.GONE
                val fragment = when(it.itemId){
                    R.id.fragment_shuttle -> ShuttleFragment()
                    R.id.fragment_bus -> BusFragment()
                    R.id.fragment_subway -> SubwayFragment()
                    R.id.fragment_cafeteria -> CafeteriaFragment()
                    else -> throw IllegalArgumentException("Unknown fragment id")
                }
                openFragment(fragment)
                return@setOnItemSelectedListener true
            }
        }
        binding.fragmentContainer.setOnClickListener {
            if (binding.additionalMenuCard.visibility == android.view.View.VISIBLE){
                binding.additionalMenuCard.visibility = android.view.View.GONE
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        if (supportFragmentManager.fragments.none { it::class == fragment::class }) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        } else {
            supportFragmentManager.fragments.first { it::class == fragment::class }.also {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }
        }
    }
}