package app.kobuggi.hyuabot.ui.bus

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
import app.kobuggi.hyuabot.databinding.FragmentBusBinding
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
class BusFragment : Fragment() {
    private val vm by viewModels<BusViewModel>()
    private lateinit var binding: FragmentBusBinding
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
        binding = FragmentBusBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm
        val busArrivalListAdapter = BusArrivalListAdapter(requireContext(), listOf()){
            routeName, routeColor -> vm.moveToTimetableFragment(routeName, routeColor)
        }
        binding.busArrivalList.adapter = busArrivalListAdapter
        binding.busArrivalList.layoutManager = LinearLayoutManager(requireContext())
        binding.busArrivalList.itemAnimator = null
        vm.busDataLiveData.observe(viewLifecycleOwner) {
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
        binding.refreshLayout.setOnRefreshListener {
            vm.stopFetchData()
            vm.startFetchData()
            binding.refreshLayout.isRefreshing = false
        }
        vm.showErrorToast.observe(viewLifecycleOwner) {
            if(it.peekContent()) {
                Toast.makeText(requireContext(), R.string.error_fetch_bus_data, Toast.LENGTH_SHORT).show()
                vm.showErrorToast.value = Event(false)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        vm.startFetchData()
        if (requireActivity() is MainActivity){
            (requireActivity() as MainActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, "Bus")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "app.kobuggi.hyuabot.ui.bus.BusFragment")
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