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
import tg.eplcoursandroid.carpooling.database.ObjetUtilisateur
import tg.eplcoursandroid.carpooling.databinding.FragmentChatBinding
import tg.eplcoursandroid.carpooling.models.Chat
import tg.eplcoursandroid.carpooling.models.Utilisateur
import tg.eplcoursandroid.carpooling.service.AuthService
import tg.eplcoursandroid.carpooling.service.ChatService
import tg.eplcoursandroid.carpooling.service.UtilisateurService

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val authService = AuthService()
    private val utilisateurService = UtilisateurService()
    private val currentUser = authService.getCurrentUser()
    private var currentUtilisateur = Utilisateur()

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
        var chatUtilisateur: MutableList<Chat> = mutableListOf()

        chatService.listerChats { chatList ->
            if (chatList != null) {

                if (isAdded) {
                    currentUtilisateur = ObjetUtilisateur.loadUtilisateur(requireContext())
                    // Vérifie si l'utilisateur est connecté
                    if (currentUtilisateur.uid == null) {
                        currentUser?.uid?.let { uid ->
                            utilisateurService.trouverUtilisateur(uid) { utilisateur ->
                                requireActivity().runOnUiThread {
                                    if (utilisateur != null) {
                                        ObjetUtilisateur.saveUtilisateur(
                                            requireContext(),
                                            utilisateur
                                        )
                                        currentUtilisateur = utilisateur
                                    } else {
                                        Log.e("HomeFragment", "Utilisateur non trouvé")
                                    }
                                }
                            }
                        } ?: Log.e("HomeFragment", "Utilisateur non trouvé")
                    }

                    chatUtilisateur.clear()

                    for (chat in chatList) {
                        if (chat.conducteurId == currentUtilisateur.uid || chat.passagerId == currentUtilisateur.uid) {
                            chatUtilisateur.add(chat)
                        }
                    }

                    if (isAdded) {
                        binding.fragmentChatRecyclerview.layoutManager =
                            LinearLayoutManager(context)
                        binding.fragmentChatRecyclerview.adapter =
                            ChatAdapter(chatUtilisateur) { chat ->
                                onChatClicked(chat)
                            }
                        binding.fragmentChatRecyclerview.setHasFixedSize(true)
                    }
                }
            } else {
                Log.d("ChatFragment","Aucun chat trouvé")
            }
        }
    }

    private fun onChatClicked(chat: Chat) {
        if (isAdded) {
            val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                putExtra(
                    "CHAT_ID",
                    "${chat.conducteurId}_${chat.passagerId}"
                ) // Génération de l'ID unique du chat
            }
            startActivity(intent)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}