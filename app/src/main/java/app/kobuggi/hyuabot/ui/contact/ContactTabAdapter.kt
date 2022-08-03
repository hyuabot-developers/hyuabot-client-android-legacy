package app.kobuggi.hyuabot.ui.contact

import androidx.viewpager2.adapter.FragmentStateAdapter

class ContactTabAdapter(fragment: ContactFragment) : FragmentStateAdapter(fragment) {
    private val inSchoolFragment = ContactTab().newInstance(0)
    private val outSchoolFragment = ContactTab().newInstance(1)

    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int) = when (position) {
        0 -> inSchoolFragment
        1 -> outSchoolFragment
        else -> throw IllegalArgumentException("Invalid position")
    }
}