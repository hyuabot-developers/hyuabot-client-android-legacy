package app.kobuggi.hyuabot.ui.main.shuttle.timetable

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.kobuggi.hyuabot.databinding.FragmentShuttleTimetableBinding


class ShuttleTimetableFragment: Fragment() {
    private lateinit var binding : FragmentShuttleTimetableBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShuttleTimetableBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        Log.d("ShuttleTimetableFragment", "onCreateView")
        return binding.root
    }
}