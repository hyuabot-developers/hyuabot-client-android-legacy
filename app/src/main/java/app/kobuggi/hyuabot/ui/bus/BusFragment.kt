package app.kobuggi.hyuabot.ui.bus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.databinding.FragmentBusBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BusFragment : Fragment() {
    private val vm by viewModels<BusViewModel>()
    private lateinit var binding: FragmentBusBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBusBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm
        val busArrivalListAdapter = BusArrivalListAdapter(requireContext(), listOf())
        binding.busArrivalList.adapter = busArrivalListAdapter
        binding.busArrivalList.layoutManager = LinearLayoutManager(requireContext())
        vm.busData.observe(viewLifecycleOwner) {
            busArrivalListAdapter.setBusTimetable(it)
            vm.isLoading.value = false
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        vm.startFetchData()
    }

    override fun onPause() {
        super.onPause()
        vm.stopFetchData()
    }
}