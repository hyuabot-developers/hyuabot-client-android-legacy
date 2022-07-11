package app.kobuggi.hyuabot.ui.main.subway

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.databinding.FragmentSubwayBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubwayFragment : Fragment() {
    private val vm by viewModels<SubwayViewModel>()
    private lateinit var binding: FragmentSubwayBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubwayBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm

        val subwayArrivalListAdapter = SubwayArrivalListAdapter(requireContext(), listOf())
        binding.subwayArrivalList.adapter = subwayArrivalListAdapter
        binding.subwayArrivalList.layoutManager = LinearLayoutManager(requireContext())
        vm.subwayData.observe(viewLifecycleOwner) {
            subwayArrivalListAdapter.setSubwayData(it)
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        vm.startFetchData()
    }

    override fun onPause() {
        super.onPause()
        vm.stopFetchData()
    }
}