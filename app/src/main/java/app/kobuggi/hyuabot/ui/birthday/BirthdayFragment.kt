package app.kobuggi.hyuabot.ui.birthday

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import app.kobuggi.hyuabot.databinding.FragmentBirthdayBinding
import app.kobuggi.hyuabot.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class BirthdayFragment : DialogFragment() {
    private lateinit var binding: FragmentBirthdayBinding
    private val vm by viewModels<BirthdayViewModel>()
    private val birthdayDialogYearPrefKey = intPreferencesKey("birthday_dialog_year")

    fun newInstance(): BirthdayFragment {
        return BirthdayFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBirthdayBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = viewLifecycleOwner
        binding.closeButton.setOnClickListener {
            dismiss()
        }
        vm.doNotOpenThisYear.observe(viewLifecycleOwner){
            val scope = CoroutineScope(Dispatchers.IO)
            if (it){
                scope.launch {
                    (requireActivity() as MainActivity).dataStore.edit {
                        preferences -> preferences[birthdayDialogYearPrefKey] = LocalDate.now().year
                    }
                }
            } else {
                scope.launch {
                    (requireActivity() as MainActivity).dataStore.edit {
                            preferences -> preferences[birthdayDialogYearPrefKey] = 0
                    }
                }
            }
        }
        return binding.root
    }

}