package app.kobuggi.hyuabot.ui.contact

import androidx.viewpager2.adapter.FragmentStateAdapter

class ContactTabAdapter(fragment: ContactFragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int) = ContactTab().newInstance(position)
}