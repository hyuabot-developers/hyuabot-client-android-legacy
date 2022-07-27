package app.kobuggi.hyuabot.ui

import android.content.DialogInterface
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import app.kobuggi.hyuabot.GlobalActivity
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : GlobalActivity(), DialogInterface.OnDismissListener {
    private val vm by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.vm = vm

        val navigationFragmentHost = supportFragmentManager.findFragmentById(R.id.fragment_container) as? NavHostFragment
        navController = navigationFragmentHost?.navController ?: return
        binding.bottomNavigationMenu.setupWithNavController(navController)
    }

    override fun onDismiss(dialogInterface: DialogInterface?) {
        recreate()
    }
}