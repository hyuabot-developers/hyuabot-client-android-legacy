package app.kobuggi.hyuabot.ui.shuttle

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.FragmentShuttleBinding
import app.kobuggi.hyuabot.ui.MainActivity
import app.kobuggi.hyuabot.ui.shuttle.timetable.ShuttleTimetable
import app.kobuggi.hyuabot.utils.Event
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShuttleFragment : Fragment(), DialogInterface.OnDismissListener {
    private val vm by viewModels<ShuttleViewModel>()
    private lateinit var binding: FragmentShuttleBinding
    private val stopList = listOf(
        ShuttleStopInfo(R.string.dormitory, LatLng(37.29339607529377, 126.83630604103446)),
        ShuttleStopInfo(R.string.shuttlecock_o, LatLng(37.29875417910844, 126.83784054072336)),
        ShuttleStopInfo(R.string.station, LatLng(37.308494476826155, 126.85310236423418)),
        ShuttleStopInfo(R.string.terminal, LatLng(37.31945164682341, 126.8455453372041)),
        ShuttleStopInfo(R.string.shuttlecock_i, LatLng(37.29869328231496, 126.8377767466817))
    )

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requestResult = tryRequestLocationPermission()
        if (!requestResult) {
            Toast.makeText(requireContext(), "위치 정보를 사용하기 위해서는 위치 정보 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        } else {
            val fusedLocationClient = requireActivity().let {
                LocationServices.getFusedLocationProviderClient(it)
            }
            if(!vm.locationChecked.value!!) {
                fusedLocationClient.lastLocation.addOnSuccessListener {
                    vm.closestStop.value = getClosestShuttleStop(it)
                    vm.locationChecked.value = true
                }
            }
            fusedLocationClient.lastLocation.addOnFailureListener {
                Toast.makeText(requireContext(), "위치 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShuttleBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm
        vm.fetchShuttleTimetable()
        vm.fetchShuttleEntireTimetable()
        checkLocationPermission()

        val shuttleInformationList = listOf(
            ShuttleInformationItem(R.drawable.hanyang_shuttle_stop, getString(R.string.closest_shuttle_stop), ""),
            ShuttleInformationItem(R.drawable.hanyang_shuttle, getString(R.string.shuttle_first_last_bus), ""),
        )
        val shuttleInformationAdapter = ShuttleInformationAdapter(requireContext(), shuttleInformationList)
        binding.shuttleTopRecyclerView.apply {
            adapter = shuttleInformationAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
        vm.closestStop.observe(viewLifecycleOwner) {
            shuttleInformationAdapter.setClosestShuttleStop(getString(it.nameID))
        }
        vm.shuttleEntireTimetable.observe(viewLifecycleOwner) {
            shuttleInformationAdapter.setShuttleFirstLastBus(it)
        }

        val shuttleArrivalListAdapter = ShuttleArrivalListAdapter(requireContext(), stopList, arrayListOf(), {
           location, titleID -> vm.clickShuttleStopLocation(location, titleID)
        }, {
            stopID, shuttleType -> vm.openShuttleTimetableFragment(stopID, shuttleType)
        })
        binding.shuttleArrivalList.adapter = shuttleArrivalListAdapter
        binding.shuttleArrivalList.layoutManager = LinearLayoutManager(requireContext())
        if(binding.shuttleArrivalList.itemAnimator is SimpleItemAnimator){
            (binding.shuttleArrivalList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
        vm.shuttleTimetable.observe(viewLifecycleOwner) {
            shuttleArrivalListAdapter.setShuttleTimetable(it)
            vm.isLoading.value = false
        }
        vm.openShuttleTimetableEvent.observe(viewLifecycleOwner) {
            if(it.peekContent() && requireActivity() is MainActivity) {
                vm.openShuttleTimetableEvent.value = Event(false)
                val shuttleTimetableItem = ShuttleTimetable(vm.shuttleStopName.value!!, vm.shuttleTimetableType.value!!)
                val action = ShuttleFragmentDirections.openShuttleTimetable(shuttleTimetableItem)
                (requireActivity() as MainActivity).navController.navigate(action)
            }
        }
        vm.isLoading.observe(viewLifecycleOwner) {
            if(it) {
                binding.shuttleArrivalProgress.visibility = View.VISIBLE
            } else {
                binding.shuttleArrivalProgress.visibility = View.GONE
            }
        }
        binding.refreshLayout.setOnRefreshListener {
            vm.stopFetchData()
            vm.startFetchData()
            binding.refreshLayout.isRefreshing = false
        }
        vm.showShuttleStopLocationDialog.observe(viewLifecycleOwner) {
            if(it.peekContent()) {
                val dialog = ShuttleStopLocationDialog().newInstance(vm.showShuttleStopLocation.value!!, vm.shuttleStopName.value!!)
                if (requireActivity() is MainActivity){
                    (requireActivity() as MainActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
                        param(FirebaseAnalytics.Param.ITEM_ID, "Shuttle Stop Location Dialog")
                        param(FirebaseAnalytics.Param.ITEM_NAME, getString(vm.shuttleStopName.value!!))
                    }
                }
                dialog.show(childFragmentManager, "ShuttleStopLocationDialog")
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        vm.showShuttleStopLocationDialog.value = Event(false)
        vm.openShuttleTimetableEvent.value = Event(false)
        vm.startFetchData()
        if(requireActivity() is MainActivity) {
            (requireActivity() as MainActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW){
                param(FirebaseAnalytics.Param.SCREEN_NAME, "Shuttle")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "ShuttleFragment")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        vm.stopFetchData()
    }

    override fun onDismiss(p0: DialogInterface?) {
        vm.showShuttleStopLocationDialog.value = Event(false)
        vm.startFetchData()
    }

    private fun checkLocationPermission() {
        val requiredPermissions = arrayListOf<String>()
        val foregroundLocationPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        if (foregroundLocationPermission != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        val foregroundPreciseLocationPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
        if (foregroundPreciseLocationPermission != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (requiredPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(requireActivity(), requiredPermissions.toTypedArray(), 0)
        }
        vm.fetchShuttleTimetable()
    }

    private fun tryRequestLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    private fun getClosestShuttleStop(location: Location): ShuttleStopInfo {
        return stopList.minBy { getDistanceFromStop(it.location, location) }
    }

    private fun getDistanceFromStop(stopLocation: LatLng, currentLocation: Location): Float {
        val location = Location("")
        location.latitude = stopLocation.latitude
        location.longitude = stopLocation.longitude
        return currentLocation.distanceTo(location)
    }
}