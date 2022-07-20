package app.kobuggi.hyuabot.ui.menu

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.FragmentMenuBinding
import app.kobuggi.hyuabot.ui.menu.info.AppInfoDialog
import app.kobuggi.hyuabot.ui.menu.theme.AppThemeDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : Fragment(){
    private val vm by viewModels<MenuViewModel>()
    private lateinit var binding: FragmentMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm

        val menuList = listOf(
            MenuButton(R.string.reading_room, R.drawable.ic_reading_room),
            MenuButton(R.string.map, R.drawable.ic_map),
            MenuButton(R.string.contact, R.drawable.ic_phone),
            MenuButton(R.string.calendar, R.drawable.ic_calendar)
        )
        val menuListAdapter = MenuListAdapter(requireContext(), menuList){
            stringID -> vm.moveToSomewhere(stringID)
        }
        binding.menuList.adapter = menuListAdapter
        binding.menuList.layoutManager = object : LinearLayoutManager(requireContext()){
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        val divider = MenuListDivider(requireContext(), R.drawable.recyclerview_divider, 20)
        binding.menuList.addItemDecoration(divider)

        val settingList = listOf(
            MenuButton(R.string.language, R.drawable.ic_language),
            MenuButton(R.string.app_theme, R.drawable.ic_dark_mode),
            MenuButton(R.string.donation, R.drawable.ic_donation),
            MenuButton(R.string.scoring, R.drawable.ic_scoring),
            MenuButton(R.string.developer_email, R.drawable.ic_email),
            MenuButton(R.string.developer_chat, R.drawable.ic_chat),
            MenuButton(R.string.about, R.drawable.ic_info),
        )
        val settingListAdapter = MenuListAdapter(requireContext(), settingList){
            stringID -> vm.moveToSomewhere(stringID)
        }
        binding.settingList.adapter = settingListAdapter
        binding.settingList.layoutManager = object : LinearLayoutManager(requireContext()){
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        binding.settingList.addItemDecoration(divider)
        vm.moveEvent.observe(viewLifecycleOwner){
            when(it.peekContent()){
                R.string.app_theme -> {
                    val dialog = AppThemeDialog()
                    dialog.show(requireActivity().supportFragmentManager, "AppThemeDialog")
                }
                R.string.donation -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://qr.kakaopay.com/FWxVPo8iO"))
                    startActivity(intent)
                }
                R.string.scoring -> {
                    val uri = Uri.parse("market://details?id=app.kobuggi.hyuabot")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                    try {
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=app.kobuggi.hyuabot")))
                    }
                }
                R.string.developer_email -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:jil8885@hanyang.ac.kr"))
                    startActivity(intent)
                }
                R.string.developer_chat -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://open.kakao.com/o/sW2kAinb"))
                    startActivity(intent)
                }
                R.string.about -> {
                    val dialog = AppInfoDialog()
                    dialog.show(requireActivity().supportFragmentManager, "AppInfoDialog")
                }
            }
        }
        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        vm.moveToSomewhere(0)
    }
}