package app.kobuggi.hyuabot.ui.main

import android.view.View
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
    lateinit var navController : NavController

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        binding.lifecycleOwner = this
        binding.vm = vm

        val navigationFragmentHost = supportFragmentManager.findFragmentById(R.id.fragment_container) as? NavHostFragment
        val navController = navigationFragmentHost?.navController ?: return
        binding.bottomNavigationMenu.setupWithNavController(navController)
    }
}