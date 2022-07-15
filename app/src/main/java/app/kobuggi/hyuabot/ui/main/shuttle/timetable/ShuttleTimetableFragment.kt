package app.kobuggi.hyuabot.ui.main.shuttle.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.FragmentShuttleTimetableBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ShuttleTimetableFragment: Fragment() {
    private lateinit var binding : FragmentShuttleTimetableBinding
    private val vm by viewModels<ShuttleTimetableViewModel>()
    private val timeDelta = hashMapOf(
        R.string.dormitory to arrayListOf(-5, -5, -5),
        R.string.shuttlecock_o to arrayListOf(0, 0, 0),
        R.string.station to arrayListOf(10, 0, 10),
        R.string.terminal to arrayListOf(0, 10, 15),
        R.string.shuttlecock_i to arrayListOf(20, 20, 25)
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShuttleTimetableBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: ShuttleTimetableFragmentArgs by navArgs()
        val context = requireContext()
        val item: ShuttleTimetable = args.shuttleTimetableItem

        val shuttleTypeID = when(item.shuttleType){
            "DH" -> R.string.shuttle_type_DH
            "DY" -> R.string.shuttle_type_DY
            "C" -> R.string.shuttle_type_C
            else -> R.string.shuttle_type_C
        }
        val timeDeltaIndex = when(item.shuttleType){
            "DH" -> 0
            "DY" -> 1
            "C" -> 2
            else -> 0
        }
        binding.shuttleTimetableToolbar.title = context.getString(
            R.string.shuttle_timetable_toolbar,
            context.getString(item.stopID),
            context.getString(shuttleTypeID)
        )

        vm.fetchShuttleTimetable()
        vm.shuttleTimetable.observe(viewLifecycleOwner){
            binding.shuttleTimetableViewpager.adapter = ShuttleTimetableTabAdapter(this, it, timeDelta[item.stopID]!![timeDeltaIndex])
            TabLayoutMediator(binding.shuttleTimetableTab, binding.shuttleTimetableViewpager) { tab, position ->
                tab.text = context.getString(
                    when(position){
                        0 -> R.string.weekdays
                        1 -> R.string.weekends
                        else -> R.string.weekdays
                    }
                )
            }.attach()
            if (it.isNotEmpty()){
                binding.shuttleTimetableProgress.visibility = View.GONE
            }
        }
    }
}