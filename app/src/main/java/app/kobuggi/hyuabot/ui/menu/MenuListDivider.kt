package app.kobuggi.hyuabot.ui.menu

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView

class MenuListDivider(private val context: Context, private val resourceID: Int, private val padding: Int) : RecyclerView.ItemDecoration() {
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + padding
        val right = parent.width - parent.paddingRight - padding
        val childCount = parent.childCount
        val resource = ResourcesCompat.getDrawable(context.resources, resourceID, null)
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + resource!!.intrinsicHeight
           resource.let {
               it.bounds = Rect(left, top, right, bottom)
                it.draw(c)
           }
        }
    }
}