package tg.eplcoursandroid.carpooling

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import tg.eplcoursandroid.carpooling.adapter.MessageAdapter
import tg.eplcoursandroid.carpooling.models.Message

class ChatActivity : AppCompatActivity() {
    private lateinit var chatId: String
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageView
    private val messages = mutableListOf<Message>()
    private lateinit var messageAdapter: MessageAdapter
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var messagesRef: DatabaseReference
    private lateinit var chatDetailsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_layout)

        // Gestion du clic sur le bouton d'envoi
        sendButton = findViewById(R.id.chat_layout_send)
        sendButton.setOnClickListener {
            envoyerMessage()
        }

        // Récupération de l'ID du chat depuis l'Intent
        chatId = intent.getStringExtra("CHAT_ID") ?: run {
            //Toast.makeText(this, "Erreur : Chat ID manquant", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialisation des références Firebase
        messagesRef = database.reference.child("chats").child(chatId).child("messages")
        chatDetailsRef = database.reference.child("chats").child(chatId).child("details")

        // Initialisation des composants UI
        messagesRecyclerView = findViewById(R.id.chat_layout_lv_messages)
        messageInput = findViewById(R.id.chat_layout_message)

        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(messages, auth.currentUser?.uid ?: "")
        messagesRecyclerView.adapter = messageAdapter

        // Charger les messages
        chargerMessages()
    }

    private fun chargerMessages() {
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messages.clear()
                snapshot.children.forEach { data ->
                    val message = data.getValue(Message::class.java)
                    if (message != null) messages.add(message)
                }
                messageAdapter.notifyDataSetChanged()
                messagesRecyclerView.scrollToPosition(messages.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity, "Erreur : ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun envoyerMessage() {
        val text = messageInput.text.toString().trim()
        if (text.isNotBlank()) {
            val messageId = messagesRef.push().key ?: return
            /*
            if (messageId == null) {
                Log.e("ChatActivity", "Erreur lors de la génération de l'ID du message")
                return
            }
            */

            val senderId = auth.currentUser?.uid ?: ""

            val message = Message(
                id = messageId,
                senderId = senderId,
                content = text,
                timestamp = System.currentTimeMillis()
            )

            messagesRef.child(messageId).setValue(message)
                .addOnSuccessListener {
                    Log.d("ChatActivity", "Message envoyé avec succès")
                    messageInput.text.clear()

                    // Mettre à jour le dernier message dans les détails du chat
                    chatDetailsRef.updateChildren(
                        mapOf("dernierMessage" to text, "timestamp" to message.timestamp)
                    )
                }
                .addOnFailureListener { e ->
                    Log.e("ChatActivity", "Erreur d'envoi du message : ${e.message}")
                }
        }
    }
}
