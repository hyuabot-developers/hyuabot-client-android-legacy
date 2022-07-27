package app.kobuggi.hyuabot.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.kobuggi.hyuabot.databinding.FragmentMapBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment() {
    private val vm by viewModels<MapViewModel>()
    private lateinit var binding: FragmentMapBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        return binding.root
    }
}