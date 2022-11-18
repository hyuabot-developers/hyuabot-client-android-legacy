package app.kobuggi.hyuabot.ui.shuttle.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.FragmentShuttleTimetableBinding
import app.kobuggi.hyuabot.ui.MainActivity
import app.kobuggi.hyuabot.utils.Event
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ShuttleTimetableFragment: Fragment() {
    private lateinit var binding : FragmentShuttleTimetableBinding
    private val vm by viewModels<ShuttleTimetableViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShuttleTimetableBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
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
            "DJ" -> R.string.shuttle_type_DJ
            else -> R.string.shuttle_type_C
        }

        binding.shuttleTimetableToolbar.title = context.getString(
            R.string.shuttle_timetable_toolbar,
            context.getString(item.stopID),
            context.getString(shuttleTypeID)
        )

        vm.fetchShuttleTimetable()
        vm.shuttleTimetable.observe(viewLifecycleOwner){
            binding.shuttleTimetableViewpager.adapter = ShuttleTimetableTabAdapter( this, it, item.stopID, shuttleTypeID)
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
            if (it.any { shuttleItem -> shuttleItem.shuttleType == item.shuttleType }){
                binding.shuttleTimetableNoData.visibility = View.GONE
            }
        }
        vm.showErrorToast.observe(viewLifecycleOwner){
            if (it.peekContent()){
                Toast.makeText(requireContext(), R.string.error_fetch_shuttle_timetable, Toast.LENGTH_SHORT).show()
                vm.showErrorToast.value = Event(false)
                binding.shuttleTimetableProgress.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(requireActivity() is MainActivity) {
            (requireActivity() as MainActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW){
                param(FirebaseAnalytics.Param.SCREEN_NAME, "Shuttle Timetable")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "ShuttleTimetableFragment")
            }
        }
    }
}