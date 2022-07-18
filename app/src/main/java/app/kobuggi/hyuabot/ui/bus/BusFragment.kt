package app.kobuggi.hyuabot.ui.bus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import app.kobuggi.hyuabot.databinding.FragmentBusBinding
import app.kobuggi.hyuabot.ui.MainActivity
import app.kobuggi.hyuabot.ui.shuttle.ShuttleFragmentDirections
import app.kobuggi.hyuabot.ui.shuttle.timetable.ShuttleTimetable
import app.kobuggi.hyuabot.utils.Event
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
        val busArrivalListAdapter = BusArrivalListAdapter(requireContext(), listOf()){
            routeName, routeColor -> vm.moveToTimetableFragment(routeName, routeColor)
        }
        binding.busArrivalList.adapter = busArrivalListAdapter
        binding.busArrivalList.layoutManager = LinearLayoutManager(requireContext())
        if(binding.busArrivalList.itemAnimator is SimpleItemAnimator){
            (binding.busArrivalList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
        vm.busData.observe(viewLifecycleOwner) {
            busArrivalListAdapter.setBusTimetable(it)
            vm.isLoading.value = false
        }
        vm.moveToTimetableFragmentEvent.observe(viewLifecycleOwner) {
            if(it.peekContent() && requireActivity() is MainActivity) {
                vm.moveToTimetableFragmentEvent.value = Event(false)
                val busTimetableItem = vm.timetableRouteName.value!!
                val busRouteColor = vm.timetableRouteColor.value!!
                val action = BusFragmentDirections.openBusTimetable(busTimetableItem, busRouteColor)
                (requireActivity() as MainActivity).navController.navigate(action)
            }
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