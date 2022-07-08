package app.kobuggi.hyuabot.ui.main.cafeteria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.kobuggi.hyuabot.databinding.FragmentCafeteriaBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CafeteriaFragment : Fragment() {
    private val vm by viewModels<CafeteriaViewModel>()
    private lateinit var binding: FragmentCafeteriaBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCafeteriaBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm
        return binding.root
    }
}