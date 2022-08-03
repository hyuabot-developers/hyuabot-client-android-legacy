package app.kobuggi.hyuabot.ui.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.FragmentContactBinding
import com.google.android.material.tabs.TabLayoutMediator
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
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm
        binding.contactViewpager.adapter = ContactTabAdapter(this)
        TabLayoutMediator(binding.contactTab, binding.contactViewpager) { tab, position ->
            tab.text = context?.getString(
                when (position) {
                    0 -> R.string.in_school
                    1 -> R.string.out_school
                    else -> R.string.out_school
                }
            )
        }.attach()
        return binding.root
    }
}