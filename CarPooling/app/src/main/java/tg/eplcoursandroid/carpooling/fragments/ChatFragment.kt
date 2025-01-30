package tg.eplcoursandroid.carpooling.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import tg.eplcoursandroid.carpooling.ChatActivity
import tg.eplcoursandroid.carpooling.R
import tg.eplcoursandroid.carpooling.adapter.ChatAdapter
import tg.eplcoursandroid.carpooling.databinding.FragmentChatBinding
import tg.eplcoursandroid.carpooling.models.Chat
import tg.eplcoursandroid.carpooling.service.ChatService

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chatService = ChatService()

        chatService.listerChats { chatList ->
            if (chatList != null) {
                binding.fragmentChatRecyclerview.layoutManager = LinearLayoutManager(context)
                binding.fragmentChatRecyclerview.adapter = ChatAdapter(chatList) { chat ->
                    onChatClicked(chat)
                }
                binding.fragmentChatRecyclerview.setHasFixedSize(true)
            } else {
                Log.d("ChatFragment","Aucun chat trouvé")
            }
        }
    }

    private fun onChatClicked(chat: Chat) {
        val intent = Intent(requireContext(), ChatActivity::class.java).apply {
            putExtra("CHAT_ID", "${chat.conducteurId}_${chat.passagerId}") // Génération de l'ID unique du chat
        }
        startActivity(intent)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}