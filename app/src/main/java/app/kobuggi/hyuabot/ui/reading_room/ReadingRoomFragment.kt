package app.kobuggi.hyuabot.ui.reading_room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.databinding.FragmentReadingRoomBinding
import app.kobuggi.hyuabot.ui.MainActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReadingRoomFragment : Fragment() {
    private val vm by viewModels<ReadingRoomViewModel>()
    private lateinit var binding: FragmentReadingRoomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReadingRoomBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        val adapter = ReadingRoomAdapter(arrayListOf())
        binding.readingRoomList.adapter = adapter
        binding.readingRoomList.layoutManager = LinearLayoutManager(requireContext())
        vm.fetchReadingRoomData()
        vm.rooms.observe(viewLifecycleOwner) {
            adapter.setReadingRooms(it)
        }
        vm.isCampus.observe(viewLifecycleOwner) {
            vm.fetchReadingRoomData()
        }
        binding.refreshLayout.setOnRefreshListener {
            vm.fetchReadingRoomData()
            binding.refreshLayout.isRefreshing = false
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if(requireActivity() is MainActivity) {
            (requireActivity() as MainActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW){
                param(FirebaseAnalytics.Param.SCREEN_NAME, "Reading Room")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "ReadingRoomFragment")
            }
        }
    }
}