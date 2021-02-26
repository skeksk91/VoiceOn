import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voiceon.Chat
import com.example.voiceon.R
import com.example.voiceon.Room
import com.example.voiceon.databinding.ChatItemBinding
import kotlin.IllegalArgumentException

@Suppress("DEPRECATION")
class ChatListAdapter(private val chats: List<Chat>,
                      private val room: Room,
                      private val clickListener: (chat: Chat) -> Unit)
    : RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>() {
    private lateinit var playBtn: ImageButton
    private lateinit var playBtnMe: ImageButton
    private lateinit var userName: TextView

    companion object {
        val TYPE_FRIEND = 0
        val TYPE_ME = 1
    }

    class ChatListViewHolder(val binding : ChatItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        when(viewType) {
            TYPE_FRIEND -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_item, parent, false)
                val viewHolder = ChatListViewHolder(ChatItemBinding.bind(view))
                playBtnMe = view.findViewById(R.id.play_btn_me)
                playBtnMe.visibility = View.INVISIBLE
                playBtn = view.findViewById(R.id.play_btn)
                userName = view.findViewById(R.id.other_user_name)
                userName.text = room.uName

                view.setOnClickListener {
                    clickListener.invoke(chats[viewHolder.adapterPosition])
                }
                return viewHolder
            }
            TYPE_ME -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_item, parent, false)
                val viewHolder = ChatListViewHolder(ChatItemBinding.bind(view))
                playBtnMe = view.findViewById(R.id.play_btn_me)

                playBtn = view.findViewById(R.id.play_btn)
                playBtn.visibility = View.INVISIBLE

                view.setOnClickListener {
                    clickListener.invoke(chats[viewHolder.adapterPosition])
                }
                return viewHolder
            }
            else -> throw IllegalArgumentException("Invalid view holder type")
        }

    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        holder.binding.chat = chats[position]
    }

    override fun getItemViewType(position: Int): Int {
        return chats[position].type
    }

    override fun getItemCount() = chats.size
}