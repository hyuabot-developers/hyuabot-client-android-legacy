package app.kobuggi.hyuabot.ui.reading_room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.ReadingRoomQuery
import app.kobuggi.hyuabot.databinding.CardReadingRoomBinding

class ReadingRoomAdapter(private var items: List<ReadingRoomQuery.ReadingRoom>) : RecyclerView.Adapter<ReadingRoomAdapter.ReadingRoomViewHolder>() {
    inner class ReadingRoomViewHolder(private val binding: CardReadingRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReadingRoomQuery.ReadingRoom) {
            binding.readingRoomName.text = item.roomName
            binding.currentSeat.text = item.availableSeat.toString()
            binding.totalSeat.text = item.activeSeat.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadingRoomViewHolder {
        return ReadingRoomViewHolder(CardReadingRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ReadingRoomViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun setReadingRooms(items: List<ReadingRoomQuery.ReadingRoom>) {
        this.items = items
        notifyDataSetChanged()
    }
}