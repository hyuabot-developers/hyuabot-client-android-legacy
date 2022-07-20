package app.kobuggi.hyuabot.ui.menu.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import app.kobuggi.hyuabot.databinding.DialogAppInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppInfoDialog : DialogFragment(){
    private lateinit var binding: DialogAppInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAppInfoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }
}