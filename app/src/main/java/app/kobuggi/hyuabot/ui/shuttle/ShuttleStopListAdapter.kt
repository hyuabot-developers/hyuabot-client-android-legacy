package app.kobuggi.hyuabot.ui.shuttle

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.CardSelectShuttleStopBinding

class ShuttleStopListAdapter(private val context: Context, private val onClickButton: (stopID: Int) -> Unit): RecyclerView.Adapter<ShuttleStopListAdapter.ViewHolder>() {
    private val shuttleStopList = mutableListOf(
        R.string.dormitory, R.string.shuttlecock_o, R.string.station, R.string.terminal, R.string.shuttlecock_i
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_select_shuttle_stop, parent, false)
        return ViewHolder(CardSelectShuttleStopBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return 5
    }

    inner class ViewHolder(private val  binding: CardSelectShuttleStopBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.shuttleStopName.text = context.getString(shuttleStopList[position])
            binding.shuttleStopCard.setOnClickListener { onClickButton(shuttleStopList[position]) }
        }
    }
}