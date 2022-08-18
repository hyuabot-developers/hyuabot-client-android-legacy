package app.kobuggi.hyuabot.ui.subway

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import app.kobuggi.hyuabot.BuildConfig
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.FragmentSubwayBinding
import app.kobuggi.hyuabot.ui.MainActivity
import app.kobuggi.hyuabot.utils.Event
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubwayFragment : Fragment() {
    private val vm by viewModels<SubwayViewModel>()
    private lateinit var binding: FragmentSubwayBinding
    private lateinit var adLoader: AdLoader
    private val nativeAdList = arrayListOf<NativeAd>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            loadAD()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubwayBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        val subwayArrivalListAdapter = SubwayArrivalListAdapter(requireContext(), listOf()){
            subwayRouteName, subwayRouteColor, subwayHeading -> vm.moveToTimetableFragment(subwayRouteName, subwayHeading, subwayRouteColor)
        }
        binding.subwayArrivalList.adapter = subwayArrivalListAdapter
        binding.subwayArrivalList.layoutManager = LinearLayoutManager(requireContext())
        if(binding.subwayArrivalList.itemAnimator is SimpleItemAnimator){
            (binding.subwayArrivalList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
        vm.subwayData.observe(viewLifecycleOwner) {
            subwayArrivalListAdapter.setSubwayData(it)
            vm.isLoading.value = false
        }
        binding.refreshLayout.setOnRefreshListener {
            vm.stopFetchData()
            vm.startFetchData()
            binding.refreshLayout.isRefreshing = false
        }
        vm.moveToTimetableFragmentEvent.observe(viewLifecycleOwner) {
            if(it.peekContent() && requireActivity() is MainActivity) {
                vm.moveToTimetableFragmentEvent.value = Event(false)
                val subwayRouteName = vm.timetableRouteName
                val subwayRouteColor = vm.timetableRouteColor
                val subwayRouteHeading = vm.timetableHeading
                val action = SubwayFragmentDirections.openSubwayTimetable(subwayRouteName.value!!, subwayRouteHeading.value!!, subwayRouteColor.value!!)
                if (requireActivity() is MainActivity){
                    (requireActivity() as MainActivity).navController.navigate(action)
                }
            }
        }
        Toast.makeText(requireContext(), R.string.click_card_to_show_timetable, Toast.LENGTH_SHORT).show()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        vm.startFetchData()
        if(requireActivity() is MainActivity) {
            (requireActivity() as MainActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW){
                param(FirebaseAnalytics.Param.SCREEN_NAME, "Subway")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "SubwayFragment")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        vm.stopFetchData()
    }

    private fun loadAD() {
        val builder = AdLoader.Builder(requireContext(), BuildConfig.ADMOB_UNIT_ID)
        adLoader =
            builder.forNativeAd { nativeAD ->
                nativeAdList.add(nativeAD)
                if (!adLoader.isLoading) {
                    insertAD()
                }
            }.withAdListener(
                object : AdListener() {
                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        if (!adLoader.isLoading){
                            insertAD()
                        }
                    }
                }).build()
        adLoader.loadAds(AdRequest.Builder().build(), 1)
    }

    private fun insertAD(){
        if (nativeAdList.isNotEmpty()){
            vm.insertAD(nativeAdList.first())
        }
    }
}