package app.kobuggi.hyuabot.ui.main.shuttle

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.databinding.FragmentShuttleBinding
import app.kobuggi.hyuabot.utils.Event
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

        val shuttleArrivalListAdapter = ShuttleArrivalListAdapter(requireContext(), arrayListOf()){
            location, titleID -> vm.clickShuttleStopLocation(location, titleID)
        }
        binding.shuttleArrivalList.adapter = shuttleArrivalListAdapter
        binding.shuttleArrivalList.layoutManager = LinearLayoutManager(requireContext())
        vm.shuttleTimetable.observe(viewLifecycleOwner) {
            shuttleArrivalListAdapter.setShuttleTimetable(it)
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

    override fun onStart() {
        super.onStart()
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
}