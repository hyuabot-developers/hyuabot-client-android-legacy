package app.kobuggi.hyuabot.ui.contact

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.FragmentContactTabBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactTab : Fragment(), DialogInterface.OnDismissListener {
    fun newInstance(position: Int): ContactTab {
        val bundle = Bundle(1)
        val fragment = ContactTab()
        bundle.putInt("position", position)
        fragment.arguments = bundle
        return fragment
    }

    private val vm by viewModels<ContactTabViewModel>()
    private lateinit var binding : FragmentContactTabBinding
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt("position") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactTabBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = viewLifecycleOwner
        vm.setPosition(position)

        val adapter = ContactListAdapter(arrayListOf()){
            previous: Int, current: Int -> setSelectedItem(previous, current)
        }
        binding.contactList.adapter = adapter
        binding.contactList.layoutManager = LinearLayoutManager(requireContext())
        vm.contactList.observe(viewLifecycleOwner) {
            adapter.setResult(it)
            Log.d("ContactTab", "contactList: ${it.size}")
            if(it.isEmpty()) {
                binding.contactList.visibility = View.GONE
                binding.contactSearchNoResult.visibility = View.VISIBLE
            } else {
                binding.contactList.visibility = View.VISIBLE
                binding.contactSearchNoResult.visibility = View.GONE
            }
        }


        return binding.root
    }

    override fun onDismiss(dialogInterface: DialogInterface) {

    }

    private fun setSelectedItem(previousPosition: Int, currentPosition: Int) {
        if(previousPosition != -1) {
            binding.contactList.findViewHolderForAdapterPosition(previousPosition)?.itemView!!.findViewById<TextView>(R.id.search_result_name).isSelected = false
        }
        binding.contactList.findViewHolderForAdapterPosition(currentPosition)?.itemView!!.findViewById<TextView>(R.id.search_result_name).isSelected = true
    }
}