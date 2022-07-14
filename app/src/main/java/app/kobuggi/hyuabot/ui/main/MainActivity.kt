package app.kobuggi.hyuabot.ui.main

import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import app.kobuggi.hyuabot.GlobalActivity
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ActivityMainBinding
import app.kobuggi.hyuabot.ui.main.bus.BusFragment
import app.kobuggi.hyuabot.ui.main.cafeteria.CafeteriaFragment
import app.kobuggi.hyuabot.ui.main.shuttle.ShuttleFragment
import app.kobuggi.hyuabot.ui.main.subway.SubwayFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : GlobalActivity(), View.OnClickListener {
    private val vm by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.vm = vm

        binding.menuButton.setOnClickListener {
            if(binding.additionalMenu.visibility == View.GONE){
                binding.additionalMenu.visibility = View.VISIBLE
            } else {
                binding.additionalMenu.visibility = View.GONE
            }
        }

        val buttonList = listOf(
            binding.shuttleButton, binding.subwayButton, binding.cafeteriaButton, binding.busButton,
            binding.readingRoomButton, binding.mapButton, binding.contactButton, binding.calendarButton,
            binding.settingButton
        )
        buttonList.forEach{button -> button.setOnClickListener(this)}
    }


    override fun onClick(view: View) {
        val fragment = when(view.id){
            R.id.shuttle_button -> ShuttleFragment()
            R.id.bus_button -> BusFragment()
            R.id.subway_button -> SubwayFragment()
            R.id.cafeteria_button -> CafeteriaFragment()
            else -> null
        }

        if (fragment != null) {
            openFragment(fragment)
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