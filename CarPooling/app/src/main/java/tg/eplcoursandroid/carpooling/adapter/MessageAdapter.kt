package tg.eplcoursandroid.carpooling.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tg.eplcoursandroid.carpooling.R
import tg.eplcoursandroid.carpooling.models.Message
import java.text.DateFormat
import java.util.Date


class MessageAdapter(private val messages: List<Message>, private val currentUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_right, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_left, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMessageViewHolder) {
            holder.messageText.text = message.text
            holder.messageTime.text = DateFormat.getTimeInstance().format(Date(message.timestamp))
        } else if (holder is ReceivedMessageViewHolder) {
            holder.messageText.text = message.text
            holder.messageTime.text = DateFormat.getTimeInstance().format(Date(message.timestamp))
        }
    }

    override fun getItemCount(): Int = messages.size

    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.item_chat_right_message)
        val messageTime: TextView = itemView.findViewById(R.id.item_chat_right_heure)
    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.item_chat_left_message)
        val messageTime: TextView = itemView.findViewById(R.id.item_chat_left_heure)
    }
}
