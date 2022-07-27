package app.kobuggi.hyuabot.ui.menu.info

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import app.kobuggi.hyuabot.databinding.DialogAppInfoBinding
import app.kobuggi.hyuabot.ui.menu.MenuFragment
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
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (parentFragment is MenuFragment){
            (parentFragment as MenuFragment).onDismiss(dialog)
        }
    }
}