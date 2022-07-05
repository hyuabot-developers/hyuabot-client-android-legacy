package app.kobuggi.hyuabot.ui.main

import androidx.activity.viewModels
import app.kobuggi.hyuabot.GlobalActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : GlobalActivity() {
    private val vm by viewModels<MainViewModel>()
}