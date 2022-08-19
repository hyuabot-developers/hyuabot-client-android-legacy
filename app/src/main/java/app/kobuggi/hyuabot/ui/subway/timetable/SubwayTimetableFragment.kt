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
import app.kobuggi.hyuabot.ui.MainActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SubwayTimetableFragment: Fragment() {
    private lateinit var binding : FragmentSubwayTimetableBinding
    private val vm by viewModels<SubwayTimetableViewModel>()
    private lateinit var callback: OnBackPressedCallback
    private val routeNameHashMap = hashMapOf(
        "4호선" to R.string.line_4,
        "수인분당선" to R.string.suin_bundang,
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubwayTimetableBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
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
        binding.toolbar.title = routeNameHashMap[routeName]?.let { context.getString(it) } ?: routeName

        vm.fetchSubwayTimetable(routeName, heading)
        vm.subwayTimetable.observe(viewLifecycleOwner){
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
        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ResourcesCompat.getColor(resources, R.color.hanyang_primary, null)
        callback.remove()
    }

    override fun onResume() {
        super.onResume()
        if(requireActivity() is MainActivity) {
            (requireActivity() as MainActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW){
                param(FirebaseAnalytics.Param.SCREEN_NAME, "Subway Timetable")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "SubwayTimetableFragment")
            }
        }
    }
}