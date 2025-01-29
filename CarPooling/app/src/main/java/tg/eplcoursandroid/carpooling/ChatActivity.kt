package tg.eplcoursandroid.carpooling

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import tg.eplcoursandroid.carpooling.adapter.MessageAdapter
import tg.eplcoursandroid.carpooling.models.Message

class ChatActivity : AppCompatActivity() {
    private lateinit var chatId: String
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private val messages = mutableListOf<Message>()
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_layout)

        // Définir un chatId fixe pour la simulation
        chatId = "simulatedChatId123"  // Remplace par un ID de test

        messagesRecyclerView = findViewById(R.id.chat_layout_lv_messages)
        messageInput = findViewById(R.id.chat_layout_message)
        sendButton = findViewById(R.id.chat_layout_send)

        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(messages, FirebaseAuth.getInstance().currentUser?.uid ?: "")
        messagesRecyclerView.adapter = messageAdapter

        // Récupérer les messages
        FirebaseDatabase.getInstance().reference.child("messages").child(chatId)
            .addValueEventListener(object : ValueEventListener {
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

        // Envoyer un message
        sendButton.setOnClickListener {
            Log.d("Bouton", "Envoyer cliquer")
            val text = messageInput.text.toString()
            if (text.isNotBlank()) {
                val messageId = FirebaseDatabase.getInstance().reference.push().key ?: return@setOnClickListener
                val message = Message(
                    id = messageId,
                    senderId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    text = text,
                    timestamp = System.currentTimeMillis()
                )

                FirebaseDatabase.getInstance().reference.child("messages").child(chatId).child(messageId).setValue(message)
                messageInput.text.clear()
            }
        }
    }
}
