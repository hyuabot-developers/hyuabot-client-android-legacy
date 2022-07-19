package app.kobuggi.hyuabot.ui.subway.timetable

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
import app.kobuggi.hyuabot.databinding.FragmentSubwayTimetableBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class SubwayTimetableFragment: Fragment() {
    private lateinit var binding : FragmentSubwayTimetableBinding
    private val vm by viewModels<SubwayTimetableViewModel>()
    private lateinit var callback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubwayTimetableBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: SubwayTimetableFragmentArgs by navArgs()
        val context = requireContext()
        val routeName: String = args.subwayRouteName
        val heading: String = args.subwayRouteHeading
        val routeColor: String = args.subwayRouteColor

        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor(routeColor)
        binding.subwayTimetableToolbar.setBackgroundColor(Color.parseColor(routeColor))
        binding.toolbar.setBackgroundColor(Color.parseColor(routeColor))
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ResourcesCompat.getColor(resources, R.color.hanyang_primary, null)
            findNavController().navigateUp()
        }
        binding.toolbar.title = routeName

        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        vm.fetchSubwayTimetable(routeName, heading)
        vm.subwayTimetable.observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                val weekdayFirst = LocalTime.parse(it.first { item -> item.weekday == "weekdays" }.departureTime, formatter)
                val weekdayLast = LocalTime.parse(it.last { item -> item.weekday == "weekdays" }.departureTime, formatter)
                val weekendsFirst = LocalTime.parse(it.first { item -> item.weekday == "weekends" }.departureTime, formatter)
                val weekendsLast = LocalTime.parse(it.last { item -> item.weekday == "weekends" }.departureTime, formatter)

                var firstLastsubwayString = context.getString(R.string.bus_route_first_last_time, context.getString(R.string.weekdays), weekdayFirst.hour.toString().padStart(2, '0'), weekdayFirst.minute.toString().padStart(2, '0'), weekdayLast.hour.toString().padStart(2, '0'), weekdayLast.minute.toString().padStart(2, '0'))
                firstLastsubwayString += "\n"
                firstLastsubwayString += context.getString(R.string.bus_route_first_last_time, context.getString(R.string.weekdays), weekendsFirst.hour.toString().padStart(2, '0'), weekendsFirst.minute.toString().padStart(2, '0'), weekendsLast.hour.toString().padStart(2, '0'), weekendsLast.minute.toString().padStart(2, '0'))
                binding.subwayRouteFirstLastTime.text = firstLastsubwayString
            }
            binding.subwayTimetableViewpager.adapter = SubwayTimetableTabAdapter( this, it)
            TabLayoutMediator(binding.subwayTimetableTab, binding.subwayTimetableViewpager) { tab, position ->
                tab.text = context.getString(
                    when(position){
                        0 -> R.string.weekdays
                        1 -> R.string.weekends
                        else -> R.string.weekdays
                    }
                )
            }.attach()
            if (it.isNotEmpty()){
                binding.subwayTimetableProgress.visibility = View.GONE
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