package tg.eplcoursandroid.carpooling.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import tg.eplcoursandroid.carpooling.R
import tg.eplcoursandroid.carpooling.models.Chat
import tg.eplcoursandroid.carpooling.service.ChatService
import tg.eplcoursandroid.carpooling.service.ConducteurService
import java.text.DateFormat
import java.util.Date

class ChatAdapter(private val chats: List<Chat>, private val onChatClick: (Chat) -> Unit) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val participantName: TextView = itemView.findViewById(R.id.item_list_chat_prenom)
        val lastMessage: TextView = itemView.findViewById(R.id.item_list_chat_dernier_message)
        val timestamp: TextView = itemView.findViewById(R.id.item_list_chat_heure)
        val profileImageView: CircleImageView = itemView.findViewById(R.id.item_list_chat_profil)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]
        val chatId = "${chat.conducteurId}_${chat.passagerId}"

        // Identifier l'autre participant (passager ou conducteur)
        val participantId = if (chat.conducteurId == FirebaseAuth.getInstance().currentUser?.uid) {
            chat.passagerId
        } else {
            chat.conducteurId
        }
        holder.participantName.text = "Utilisateur : $participantId"

        // Charger le dernier message depuis Firebase
        ChatService().obtenirDernierMessage(chatId) { message ->
            holder.lastMessage.text = message?.content ?: "Aucun message"
            holder.timestamp.text = DateFormat.getDateTimeInstance().format(Date(message?.timestamp ?: 0))
            val conducteurService = ConducteurService()

            conducteurService.trouverConducteur(chat.conducteurId) { conducteur ->
                if (conducteur != null) {
                    // Si le conducteur est trouvé
                    println("Conducteur trouvé : ${conducteur.utilisateur?.nom}")

                    // Charger l'image du conducteur
                    Glide.with(holder.itemView.context)
                        .load(conducteur.utilisateur?.photoUrl) // Image du conducteur
                        .into(holder.profileImageView)

                } else {
                    // Si le conducteur n'est pas trouvé
                    println("Conducteur non trouvé")
                }
            }
        }

        holder.itemView.setOnClickListener {
            onChatClick(chat)
        }
    }

    override fun getItemCount() = chats.size
}
