package app.kobuggi.hyuabot.ui.reading_room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.ReadingRoomQuery
import app.kobuggi.hyuabot.databinding.CardReadingRoomBinding

class ReadingRoomAdapter(private var items: List<ReadingRoomQuery.ReadingRoom>) : RecyclerView.Adapter<ReadingRoomAdapter.ReadingRoomViewHolder>() {
    private val roomNameStringID = mapOf(
        "제1열람실 (2F)" to R.string.room_name_1_2f,
        "제2열람실 (4F)" to R.string.room_name_2_4f,
        "제3열람실 (4F)" to R.string.room_name_3_4f,
        "HOLMZ 열람석" to R.string.holmz_room,
        "법학 제1열람실[3층]" to R.string.law_room_1_3f,
        "법학 제2열람실A[4층]" to R.string.law_room_2a_4f,
        "법학 제2열람실B[4층]" to R.string.law_room_2b_4f,
        "제1열람실[지하1층]" to R.string.room_name_1_underground_1f,
        "제2열람실[지하1층]" to R.string.room_name_2_underground_1f,
        "제3열람실[3층]" to R.string.room_name_3_3f,
    )


    inner class ReadingRoomViewHolder(private val binding: CardReadingRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReadingRoomQuery.ReadingRoom) {
            binding.readingRoomName.text = if(roomNameStringID.containsKey(item.roomName)) {
                binding.root.context.getString(roomNameStringID[item.roomName]!!)
            } else {
                item.roomName
            }
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