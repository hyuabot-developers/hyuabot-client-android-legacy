package app.kobuggi.hyuabot.ui.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.kobuggi.hyuabot.databinding.FragmentContactBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactFragment : Fragment() {
    private val vm by viewModels<ContactViewModel>()
    private lateinit var binding: FragmentContactBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm

        return binding.root
    }
}