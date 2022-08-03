package app.kobuggi.hyuabot.ui.contact

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.kobuggi.hyuabot.databinding.FragmentContactTabBinding

class ContactTab : Fragment(), DialogInterface.OnDismissListener {
    private val vm by viewModels<ContactTabViewModel>()
    private lateinit var binding : FragmentContactTabBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactTabBinding.inflate(inflater, container, false)
        binding.vm = vm
        return binding.root
    }

    override fun onDismiss(dialogInterface: DialogInterface) {

    }
}