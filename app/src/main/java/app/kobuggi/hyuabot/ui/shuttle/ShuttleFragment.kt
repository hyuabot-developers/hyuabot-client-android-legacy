package app.kobuggi.hyuabot.ui.shuttle

import android.Manifest
import android.content.DialogInterface
import android.location.Location
import android.os.Bundle
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShuttleFragment : Fragment(), DialogInterface.OnDismissListener {
    private val vm by viewModels<ShuttleViewModel>()
    private lateinit var binding: FragmentShuttleBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShuttleBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm

        checkLocationPermission()
        val shuttleArrivalListAdapter = ShuttleArrivalListAdapter(requireContext(), arrayListOf(), arrayListOf(), {
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

        val requestResult = tryRequestLocationPermission()
        if (!requestResult) {
            shuttleArrivalListAdapter.setShuttleStopList(vm.sortedStopList.value!!)
            Toast.makeText(requireContext(), "위치 정보를 사용하기 위해서는 위치 정보 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        } else {
            val fusedLocationClient = requireActivity().let {
                LocationServices.getFusedLocationProviderClient(it)
            }
            if(!vm.locationChecked.value!!) {
                fusedLocationClient.lastLocation.addOnSuccessListener {
                    if(it != null) {
                        vm.sortedStopList.value = getSortedStopList(it)
                        Toast.makeText(requireContext(), "가장 가까운 셔틀버스 정류장은 ${getString(vm.sortedStopList.value!![0].nameID)}입니다.", Toast.LENGTH_SHORT).show()
                        shuttleArrivalListAdapter.setShuttleStopList(vm.sortedStopList.value!!)
                        vm.locationChecked.value = true
                    } else {
                        shuttleArrivalListAdapter.setShuttleStopList(vm.sortedStopList.value!!)
                    }
                }
            }
            fusedLocationClient.lastLocation.addOnFailureListener {
                shuttleArrivalListAdapter.setShuttleStopList(vm.sortedStopList.value!!)
                Toast.makeText(requireContext(), "위치 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        vm.showShuttleStopLocationDialog.observe(viewLifecycleOwner) {
            if(it.peekContent()) {
                val dialog = ShuttleStopLocationDialog().newInstance(vm.showShuttleStopLocation.value!!, vm.shuttleStopName.value!!)
                dialog.show(requireActivity().supportFragmentManager, "ShuttleStopLocationDialog")
            }
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

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        vm.showShuttleStopLocationDialog.value = Event(false)
        vm.openShuttleTimetableEvent.value = Event(false)
        vm.startFetchData()
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

    private fun getSortedStopList(location: Location): List<ShuttleStopInfo> {
        return vm.sortedStopList.value!!.sortedBy { getDistanceFromStop(it.location, location) }
    }

    private fun getDistanceFromStop(stopLocation: LatLng, currentLocation: Location): Float {
        val location = Location("")
        location.latitude = stopLocation.latitude
        location.longitude = stopLocation.longitude
        return currentLocation.distanceTo(location)
    }
}