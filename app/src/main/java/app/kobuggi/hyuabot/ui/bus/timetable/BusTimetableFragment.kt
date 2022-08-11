package app.kobuggi.hyuabot.ui.bus.timetable

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.FragmentBusTimetableBinding
import app.kobuggi.hyuabot.ui.MainActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class BusTimetableFragment: Fragment() {
    private lateinit var binding : FragmentBusTimetableBinding
    private val vm by viewModels<BusTimetableViewModel>()
    private lateinit var callback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBusTimetableBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: BusTimetableFragmentArgs by navArgs()
        val context = requireContext()
        val routeName: String = args.busTimetableRoute
        val routeColor: String = args.busRouteColor

        if(requireActivity() is MainActivity) {
            (requireActivity() as MainActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW){
                param(FirebaseAnalytics.Param.SCREEN_NAME, "Bus Timetable")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "BusTimetableFragment")
            }
        }

        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor(routeColor)
        binding.busTimetableToolbar.setBackgroundColor(Color.parseColor(routeColor))
        binding.toolbar.setBackgroundColor(Color.parseColor(routeColor))
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ResourcesCompat.getColor(resources, R.color.hanyang_primary, null)
            findNavController().navigateUp()
        }
        binding.toolbar.title = routeName
        binding.busRouteInterval.text = when(routeName){
            "10-1" -> context.getString(R.string.bus_route_interval, 25, 50)
            "707-1" -> context.getString(R.string.bus_route_interval, 120, 240)
            "3102" -> context.getString(R.string.bus_route_interval, 15, 30)
            else -> context.getString(R.string.bus_route_interval,0, 0)
        }
        binding.busRoute.text = when(routeName){
            "10-1" -> context.getString(R.string.bus_route, context.getString(R.string.purgio_6th), context.getString(R.string.sangnoksu_station))
            "707-1" -> context.getString(R.string.bus_route, context.getString(R.string.new_ansan_college), context.getString(R.string.suwon_station))
            "3102" -> context.getString(R.string.bus_route, context.getString(R.string.saesol_high_school), context.getString(R.string.gangnam_station))
            else -> ""
        }

        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        vm.fetchBusTimetable(routeName)
        vm.busTimetable.observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                val weekdayFirst = LocalTime.parse(it.first { item -> item.weekday == "weekdays" }.departureTime.toString(), formatter)
                val weekdayLast = LocalTime.parse(it.last { item -> item.weekday == "weekdays" }.departureTime.toString(), formatter)
                val saturdayFirst = LocalTime.parse(it.first { item -> item.weekday == "saturday" }.departureTime.toString(), formatter)
                val saturdayLast = LocalTime.parse(it.last { item -> item.weekday == "saturday" }.departureTime.toString(), formatter)
                val sundayFirst = LocalTime.parse(it.first { item -> item.weekday == "sunday" }.departureTime.toString(), formatter)
                val sundayLast = LocalTime.parse(it.last { item -> item.weekday == "sunday" }.departureTime.toString(), formatter)

                var firstLastBusString = context.getString(R.string.bus_route_first_last_time, context.getString(R.string.weekdays), weekdayFirst.hour.toString().padStart(2, '0'), weekdayFirst.minute.toString().padStart(2, '0'), weekdayLast.hour.toString().padStart(2, '0'), weekdayLast.minute.toString().padStart(2, '0'))
                firstLastBusString += "\n"
                firstLastBusString += context.getString(R.string.bus_route_first_last_time, context.getString(R.string.saturday), saturdayFirst.hour.toString().padStart(2, '0'), saturdayFirst.minute.toString().padStart(2, '0'), saturdayLast.hour.toString().padStart(2, '0'), saturdayLast.minute.toString().padStart(2, '0'))
                firstLastBusString += "\n"
                firstLastBusString += context.getString(R.string.bus_route_first_last_time, context.getString(R.string.sunday), sundayFirst.hour.toString().padStart(2, '0'), sundayFirst.minute.toString().padStart(2, '0'), sundayLast.hour.toString().padStart(2, '0'), sundayLast.minute.toString().padStart(2, '0'))
                binding.busRouteFirstLastTime.text = firstLastBusString
            }
            binding.busTimetableViewpager.adapter = BusTimetableTabAdapter( this, it)
            TabLayoutMediator(binding.busTimetableTab, binding.busTimetableViewpager) { tab, position ->
                tab.text = context.getString(
                    when(position){
                        0 -> R.string.weekdays
                        1 -> R.string.saturday
                        2 -> R.string.sunday
                        else -> R.string.weekdays
                    }
                )
            }.attach()
            if (it.isNotEmpty()){
                binding.busTimetableProgress.visibility = View.GONE
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val window = requireActivity().window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ResourcesCompat.getColor(resources, R.color.hanyang_primary, null)
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}