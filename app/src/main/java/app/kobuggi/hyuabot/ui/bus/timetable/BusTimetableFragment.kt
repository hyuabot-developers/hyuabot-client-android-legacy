package app.kobuggi.hyuabot.ui.bus.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.FragmentBusTimetableBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BusTimetableFragment: Fragment() {
    private lateinit var binding : FragmentBusTimetableBinding
    private val vm by viewModels<BusTimetableViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBusTimetableBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: BusTimetableFragmentArgs by navArgs()
        val context = requireContext()
        val routeName: String = args.busTimetableRoute


        vm.fetchBusTimetable(routeName)
        vm.busTimetable.observe(viewLifecycleOwner){
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
}