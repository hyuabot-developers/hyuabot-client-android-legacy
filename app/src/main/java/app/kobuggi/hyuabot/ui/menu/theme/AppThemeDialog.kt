package app.kobuggi.hyuabot.ui.menu.theme

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import app.kobuggi.hyuabot.databinding.DialogAppThemeBinding
import app.kobuggi.hyuabot.ui.menu.MenuFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppThemeDialog : DialogFragment(){
    private lateinit var binding: DialogAppThemeBinding
    private val vm by viewModels<AppThemeDialogViewModel>()
    private val themePrefKey = stringPreferencesKey("theme")
    @Inject lateinit var dataStore: DataStore<Preferences>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAppThemeBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = viewLifecycleOwner

        val scope = CoroutineScope(Dispatchers.IO)
        vm.darkmode.observe(viewLifecycleOwner){
            scope.launch {
                dataStore.edit {
                    preferences -> preferences[themePrefKey] = it
                }
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