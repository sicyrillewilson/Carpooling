package tg.eplcoursandroid.carpooling.service

import com.google.firebase.database.FirebaseDatabase
import tg.eplcoursandroid.carpooling.models.Chat

class ChatService {

    fun ajouterChat(chat: Chat) {
        val database = FirebaseDatabase.getInstance()
        //val ref = database.getReference("chats/${chat.idChat}")
        val ref = database.getReference("chats/${chat.id}")
        ref.setValue(chat)
            .addOnSuccessListener {
                println("Chat ajouté avec succès !")
            }
            .addOnFailureListener { e ->
                println("Erreur : ${e.message}")
            }
    }

    fun supprimerChat(idChat: String) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("chats/$idChat")
        ref.removeValue()
            .addOnSuccessListener {
                println("Chat supprimé avec succès !")
            }
            .addOnFailureListener { e ->
                println("Erreur : ${e.message}")
            }
    }

    fun modifierChat(chat: Chat) {
        val database = FirebaseDatabase.getInstance()
        //val ref = database.getReference("chats/${chat.idChat}")
        val ref = database.getReference("chats/${chat.id}")
        ref.setValue(chat)
            .addOnSuccessListener {
                println("Chat modifié avec succès !")
            }
            .addOnFailureListener { e ->
                println("Erreur : ${e.message}")
            }
    }

    fun trouverChat(idChat: String, callback: (Chat?) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("chats/$idChat")
        ref.get().addOnSuccessListener { snapshot ->
            val chat = snapshot.getValue(Chat::class.java)
            callback(chat)
        }.addOnFailureListener { e ->
            println("Erreur : ${e.message}")
            callback(null)
        }
    }

    fun listerChats(callback: (List<Chat>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("chats")
        ref.get().addOnSuccessListener { snapshot ->
            val chats = snapshot.children.mapNotNull { it.getValue(Chat::class.java) }
            callback(chats)
        }.addOnFailureListener { e ->
            println("Erreur : ${e.message}")
            callback(emptyList())
        }
    }
}