package app.kobuggi.hyuabot.ui.main.shuttle.timetable

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import app.kobuggi.hyuabot.R
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: ShuttleTimetableFragmentArgs by navArgs()
        val context = requireContext()
        val item: ShuttleTimetable = args.shuttleTimetableItem

        val shuttleTypeID = when(item.shuttleType){
            "DH" -> R.string.shuttle_type_DH
            "DY" -> R.string.shuttle_type_DY
            "C" -> R.string.shuttle_type_C
            else -> R.string.shuttle_type_C
        }
        binding.shuttleTimetableToolbar.title = context.getString(
            R.string.shuttle_timetable_toolbar,
            context.getString(item.stopID),
            context.getString(shuttleTypeID)
        )
        Log.d("ShuttleTimetableFragment", binding.shuttleTimetableToolbar.title.toString())
    }
}