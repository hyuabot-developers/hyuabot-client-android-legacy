package app.kobuggi.hyuabot.ui.cafeteria

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import app.kobuggi.hyuabot.BuildConfig
import app.kobuggi.hyuabot.databinding.FragmentCafeteriaBinding
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
class CafeteriaFragment : Fragment(), DialogInterface.OnDismissListener {
    private val vm by viewModels<CafeteriaViewModel>()
    private lateinit var binding: FragmentCafeteriaBinding
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
        binding = FragmentCafeteriaBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        val cafeteriaListAdapter = CafeteriaListAdapter(requireContext(), arrayListOf()){
            location, title -> vm.clickCafeteriaLocation(location, title)
        }
        binding.cafeteriaMenuList.adapter = cafeteriaListAdapter
        binding.cafeteriaMenuList.layoutManager = LinearLayoutManager(context)
        if(binding.cafeteriaMenuList.itemAnimator is SimpleItemAnimator){
            (binding.cafeteriaMenuList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
        vm.fetchData()
        vm.cafeteriaMenu.observe(viewLifecycleOwner){
            cafeteriaListAdapter.setCafeteriaList(it)
            vm.isLoading.value = false
            if (it.isEmpty()){
                binding.cafeteriaNoData.visibility = View.VISIBLE
                binding.cafeteriaMenuList.visibility = View.GONE
            } else {
                binding.cafeteriaNoData.visibility = View.GONE
                binding.cafeteriaMenuList.visibility = View.VISIBLE
            }
        }

        vm.showCafeteriaLocationDialog.observe(viewLifecycleOwner) {
            if(it.peekContent() && vm.cafeteriaLocation.value != null && vm.cafeteriaName.value != null) {
                val dialog = CafeteriaLocationDialog(vm.cafeteriaLocation.value!!, vm.cafeteriaName.value!!)
                if (requireActivity() is MainActivity){
                    (requireActivity() as MainActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT){
                        param(FirebaseAnalytics.Param.ITEM_ID, "Cafeteria Location Dialog")
                        param(FirebaseAnalytics.Param.ITEM_NAME, vm.cafeteriaName.value!!)
                    }
                }
                dialog.show(childFragmentManager, "cafeteria_location_dialog")
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        vm.showCafeteriaLocationDialog.value = Event(false)
        if (requireActivity() is MainActivity){
            (requireActivity() as MainActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW){
                param(FirebaseAnalytics.Param.SCREEN_NAME, "Cafeteria")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "CafeteriaFragment")
            }
        }
    }

    override fun onDismiss(p0: DialogInterface?) {
        vm.showCafeteriaLocationDialog.value = Event(false)
        vm.fetchData()
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