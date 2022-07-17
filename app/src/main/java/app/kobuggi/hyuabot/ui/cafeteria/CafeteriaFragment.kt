package app.kobuggi.hyuabot.ui.cafeteria

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.databinding.FragmentCafeteriaBinding
import app.kobuggi.hyuabot.utils.Event
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CafeteriaFragment : Fragment(), DialogInterface.OnDismissListener {
    private val vm by viewModels<CafeteriaViewModel>()
    private lateinit var binding: FragmentCafeteriaBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCafeteriaBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm

        val cafeteriaListAdapter = CafeteriaListAdapter(requireContext(), arrayListOf()){
            location, title -> vm.clickCafeteriaLocation(location, title)
        }
        binding.cafeteriaMenuList.adapter = cafeteriaListAdapter
        binding.cafeteriaMenuList.layoutManager = LinearLayoutManager(context)

        vm.fetchData()
        vm.cafeteriaMenu.observe(viewLifecycleOwner){
            cafeteriaListAdapter.setCafeteriaList(it)
            vm.isLoading.value = false
        }

        vm.showCafeteriaLocationDialog.observe(viewLifecycleOwner) {
            if(it.peekContent() && vm.cafeteriaLocation.value != null && vm.cafeteriaName.value != null) {
                val dialog = CafeteriaLocationDialog(vm.cafeteriaLocation.value!!, vm.cafeteriaName.value!!)
                dialog.show(requireActivity().supportFragmentManager, "cafeteria_location_dialog")
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        vm.showCafeteriaLocationDialog.value = Event(false)
    }

    override fun onDismiss(p0: DialogInterface?) {
        vm.showCafeteriaLocationDialog.value = Event(false)
        vm.fetchData()
    }
}