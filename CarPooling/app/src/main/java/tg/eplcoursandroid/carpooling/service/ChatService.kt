package tg.eplcoursandroid.carpooling.service

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import tg.eplcoursandroid.carpooling.models.Chat
import tg.eplcoursandroid.carpooling.models.Message

class ChatService {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val chatRef: DatabaseReference = database.getReference("chats")

    // Ajouter un chat (si déjà existant, ne rien faire)
    fun ajouterChat(chat: Chat) {
        val chatId = "${chat.conducteurId}_${chat.passagerId}"
        val chatDetailsRef = chatRef.child(chatId).child("details")

        chatDetailsRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                chatDetailsRef.setValue(chat)
                    .addOnSuccessListener {
                        Log.d("ChatService", "Chat ajouté avec succès !")
                    }
                    .addOnFailureListener { e ->
                        Log.e("ChatService", "Erreur : ${e.message}")
                    }
            }
        }
    }

    // Supprimer un chat
    fun supprimerChat(idChat: String) {
        chatRef.child(idChat).removeValue()
            .addOnSuccessListener {
                Log.d("ChatService", "Chat supprimé avec succès !")
            }
            .addOnFailureListener { e ->
                Log.e("ChatService", "Erreur : ${e.message}")
            }
    }

    // Modifier un chat (ex : mise à jour du dernier message)
    fun modifierChat(chat: Chat) {
        chatRef.child(chat.id).child("details").setValue(chat)
            .addOnSuccessListener {
                Log.d("ChatService", "Chat modifié avec succès !")
            }
            .addOnFailureListener { e ->
                Log.e("ChatService", "Erreur : ${e.message}")
            }
    }

    // Trouver un chat par ID
    fun trouverChat(idChat: String, callback: (Chat?) -> Unit) {
        chatRef.child(idChat).child("details").get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.getValue(Chat::class.java))
            }
            .addOnFailureListener { e ->
                Log.e("ChatService", "Erreur : ${e.message}")
                callback(null)
            }
    }

    // Lister tous les chats
    fun listerChats(callback: (List<Chat>) -> Unit) {
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chats = snapshot.children.mapNotNull { it.child("details").getValue(Chat::class.java) }
                callback(chats)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatService", "Erreur : ${error.message}")
                callback(emptyList())
            }
        })
    }

    // Ajouter un message à un chat
    fun ajouterMessage(chatId: String, message: Message) {
        val messageRef = chatRef.child(chatId).child("messages").push()
        message.id = messageRef.key ?: ""

        messageRef.setValue(message)
            .addOnSuccessListener {
                Log.d("ChatService", "Message ajouté avec succès !")

                // Mettre à jour le dernier message dans le chat
                chatRef.child(chatId).child("details").updateChildren(
                    mapOf("dernierMessage" to message.content, "timestamp" to message.timestamp)
                )
            }
            .addOnFailureListener { e ->
                Log.e("ChatService", "Erreur : ${e.message}")
            }
    }

    // Récupérer le dernier message d'un chat
    fun obtenirDernierMessage(chatId: String, callback: (Message?) -> Unit) {
        chatRef.child(chatId).child("messages")
            .orderByChild("timestamp").limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dernierMessage = snapshot.children.firstOrNull()?.getValue(Message::class.java)
                    callback(dernierMessage)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatService", "Erreur : ${error.message}")
                    callback(null)
                }
            })
    }
}