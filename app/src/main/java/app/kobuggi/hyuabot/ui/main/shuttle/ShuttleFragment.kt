package app.kobuggi.hyuabot.ui.main.shuttle

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
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.FragmentShuttleBinding
import app.kobuggi.hyuabot.utils.Event
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShuttleFragment : Fragment(), DialogInterface.OnDismissListener {
    private val vm by viewModels<ShuttleViewModel>()
    private lateinit var binding: FragmentShuttleBinding
    private val shuttleStopNameList = listOf(R.string.dormitory, R.string.shuttlecock_o, R.string.station, R.string.terminal, R.string.shuttlecock_i)
    private val stopLocationList = listOf(
        LatLng(37.29339607529377, 126.83630604103446),
        LatLng(37.29875417910844, 126.83784054072336),
        LatLng(37.308494476826155, 126.85310236423418),
        LatLng(37.31945164682341, 126.8455453372041),
        LatLng(37.29869328231496, 126.8377767466817)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShuttleBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm

        checkLocationPermission()

        val shuttleArrivalListAdapter = ShuttleArrivalListAdapter(requireContext(), arrayListOf()){
            location, titleID -> vm.clickShuttleStopLocation(location, titleID)
        }
        binding.shuttleArrivalList.adapter = shuttleArrivalListAdapter
        binding.shuttleArrivalList.layoutManager = LinearLayoutManager(requireContext())
        vm.shuttleTimetable.observe(viewLifecycleOwner) {
            shuttleArrivalListAdapter.setShuttleTimetable(it)
        }

        val requestResult = tryRequestLocationPermission()
        if (!requestResult) {
            Toast.makeText(requireContext(), "위치 정보를 사용하기 위해서는 위치 정보 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        } else {
            val fusedLocationClient = requireActivity().let {
                LocationServices.getFusedLocationProviderClient(it)
            }
            fusedLocationClient.lastLocation.addOnSuccessListener {
                val index = getClosestShuttleStop(it)
                Toast.makeText(requireContext(), "가장 가까운 셔틀버스 정류장은 ${getString(shuttleStopNameList[index])}입니다.", Toast.LENGTH_SHORT).show()
                binding.shuttleArrivalList.smoothScrollToPosition(index)
                shuttleArrivalListAdapter.setClosestShuttleStop(index)
            }
        }


        vm.showShuttleStopLocationDialog.observe(viewLifecycleOwner) {
            if(it.peekContent()) {
                val dialog = ShuttleStopLocationDialog(vm.showShuttleStopLocation.value!!, vm.shuttleStopName.value!!)
                dialog.show(requireActivity().supportFragmentManager, "ShuttleStopLocationDialog")
                requireActivity().supportFragmentManager.executePendingTransactions()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        vm.showShuttleStopLocationDialog.value = Event(false)
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

    private fun getClosestShuttleStop(location: Location): Int {
        val shuttleStops = stopLocationList
        val shuttleStop = shuttleStops.minBy {
            val stop = Location("")
            stop.latitude = it.latitude
            stop.longitude = it.longitude
            val distance = stop.distanceTo(location)
            distance
        }
        return stopLocationList.indexOf(shuttleStop)
    }
}