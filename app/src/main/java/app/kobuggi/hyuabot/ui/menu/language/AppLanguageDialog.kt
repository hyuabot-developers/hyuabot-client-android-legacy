package app.kobuggi.hyuabot.ui.menu.language

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import app.kobuggi.hyuabot.databinding.DialogLanguageBinding
import app.kobuggi.hyuabot.ui.menu.MenuFragment
import app.kobuggi.hyuabot.utils.LocaleHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppLanguageDialog : DialogFragment(){
    private lateinit var binding: DialogLanguageBinding
    private val vm by viewModels<AppLanguageDialogViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLanguageBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = viewLifecycleOwner

        val sharedPreferences = requireActivity().getSharedPreferences("hyuabot", 0)
        vm.localeCode.observe(viewLifecycleOwner) {
            LocaleHelper.setLocale(it)
            sharedPreferences.edit().apply {
                putString("locale", it)
                apply()
            }
            dismiss()
        }
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (parentFragment is MenuFragment){
            (parentFragment as MenuFragment).onDismiss(dialog)
        }
    }
}