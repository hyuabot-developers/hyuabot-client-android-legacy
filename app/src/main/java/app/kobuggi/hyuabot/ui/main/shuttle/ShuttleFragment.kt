package app.kobuggi.hyuabot.ui.main.shuttle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.kobuggi.hyuabot.databinding.FragmentShuttleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShuttleFragment : Fragment() {
    private val vm by viewModels<ShuttleViewModel>()
    private lateinit var binding: FragmentShuttleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShuttleBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm
        return binding.root
    }
}