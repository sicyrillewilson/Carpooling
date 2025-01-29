package tg.eplcoursandroid.carpooling.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import tg.eplcoursandroid.carpooling.R
import tg.eplcoursandroid.carpooling.models.Chat
import java.text.DateFormat
import java.util.Date

class ChatAdapter(private val chats: List<Chat>, private val onChatClick: (Chat) -> Unit) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val participantName: TextView = itemView.findViewById(R.id.participantName)
        val lastMessage: TextView = itemView.findViewById(R.id.dernierMessage)
        val timestamp: TextView = itemView.findViewById(R.id.timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]

        // Nom du participant (passager ou conducteur)
        val participantId = if (chat.conducteurId == FirebaseAuth.getInstance().currentUser?.uid) {
            chat.passagerId
        } else {
            chat.conducteurId
        }
        holder.participantName.text = "Utilisateur : $participantId"

        holder.lastMessage.text = chat.dernierMessage
        holder.timestamp.text = DateFormat.getDateTimeInstance().format(Date(chat.timestamp))

        holder.itemView.setOnClickListener {
            onChatClick(chat)
        }
    }

    override fun getItemCount() = chats.size
}
