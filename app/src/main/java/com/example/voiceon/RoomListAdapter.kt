package com.example.voiceon

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.voiceon.databinding.RoomItemBinding

@Suppress("DEPRECATION")
class RoomListAdapter(private var rooms: List<Room>,
                      private val clickListener: (room: Room) -> Unit,
                      private val longClickListener: (Room) -> Unit)
    : RecyclerView.Adapter<RoomListAdapter.RoomListViewHolder>() {
    private lateinit var favoriteView: ImageView

    class RoomListViewHolder(val binding : RoomItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        Log.e("jake", "onAttachedToRecyclerView")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.room_item, parent, false)
        val viewHolder = RoomListViewHolder(RoomItemBinding.bind(view))

        view.setOnClickListener {
            clickListener.invoke(rooms[viewHolder.adapterPosition])
        }
        view.setOnLongClickListener {
            true
        }

        Log.e("jake", "rooms size = " + rooms.size)

        favoriteView = view.findViewById(R.id.favorite)
        favoriteView.setOnClickListener {
            rooms[viewHolder.adapterPosition].isLike = !rooms[viewHolder.adapterPosition].isLike!!
            notifyItemChanged(viewHolder.adapterPosition)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RoomListViewHolder, position: Int) {
        holder.binding.room = rooms[position]
    }

    override fun getItemCount() = rooms.size
}