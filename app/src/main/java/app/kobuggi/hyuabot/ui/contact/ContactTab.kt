package app.kobuggi.hyuabot.ui.contact

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
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
    private val parentViewModel : ContactViewModel by viewModels({requireParentFragment()})
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

        val adapter = ContactListAdapter(arrayListOf(),
            { contactItem: ContactItem -> callToNumber(contactItem.name, contactItem.phone) },
            { previousPosition: Int, currentPosition: Int -> setSelectedItem(previousPosition, currentPosition) })
        binding.contactList.adapter = adapter
        binding.contactList.layoutManager = LinearLayoutManager(requireContext())
        binding.contactList.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        vm.contactList.observe(viewLifecycleOwner) {
            adapter.setResult(it)
            if(it.isEmpty()) {
                binding.contactList.visibility = View.GONE
                binding.contactSearchNoResult.visibility = View.VISIBLE
            } else {
                binding.contactList.visibility = View.VISIBLE
                binding.contactSearchNoResult.visibility = View.GONE
            }
        }

        parentViewModel.queryString.observe(viewLifecycleOwner) {
            vm.queryContact(position, it)
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

    private fun callToNumber(name: String, phone: String) {
        Toast.makeText(requireContext(), requireContext().getString(R.string.call_dialog_message, name, phone), Toast.LENGTH_SHORT).show()
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), requireContext().getString(R.string.no_dial_app), Toast.LENGTH_SHORT).show()
        }
    }
}