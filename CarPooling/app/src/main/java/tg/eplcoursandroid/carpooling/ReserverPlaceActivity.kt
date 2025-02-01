@file:Suppress("DEPRECATION")

package tg.eplcoursandroid.carpooling

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import tg.eplcoursandroid.carpooling.database.ObjetConducteur
import tg.eplcoursandroid.carpooling.database.ObjetUtilisateur
import tg.eplcoursandroid.carpooling.databinding.DevenirChauffeurLayoutBinding
import tg.eplcoursandroid.carpooling.databinding.ReserverPlaceBinding
import tg.eplcoursandroid.carpooling.databinding.SignInBinding
import tg.eplcoursandroid.carpooling.models.Chat
import tg.eplcoursandroid.carpooling.models.Conducteur
import tg.eplcoursandroid.carpooling.models.Passager
import tg.eplcoursandroid.carpooling.models.Trajet
import tg.eplcoursandroid.carpooling.models.Utilisateur
import tg.eplcoursandroid.carpooling.service.AuthService
import tg.eplcoursandroid.carpooling.service.ChatService
import tg.eplcoursandroid.carpooling.service.ConducteurService
import tg.eplcoursandroid.carpooling.service.PassagerService
import tg.eplcoursandroid.carpooling.service.TrajetService
import tg.eplcoursandroid.carpooling.service.UtilisateurService

class ReserverPlaceActivity : AppCompatActivity() {

    lateinit var password: String
    lateinit private var fbauth: FirebaseAuth
    lateinit private var pds: ProgressDialog
    lateinit var binding : ReserverPlaceBinding

    private val authService = AuthService()
    private val passagerService = PassagerService()
    private val utilisateurService = UtilisateurService()
    private val conducteurService = ConducteurService()
    private val chatService = ChatService()
    private val currentUser = authService.getCurrentUser()
    private var currentUtilisateur = Utilisateur()
    private var trajetService = TrajetService()

    lateinit private var numero : EditText
    private lateinit var matricule : EditText
    private lateinit var annuler : Button
    private lateinit var creer : Button
    private var trajet: Trajet = Trajet()



    // Ajout de la variable pour suivre l'état du mot de passe
    override fun onCreate(savedInstanceState: Bundle?) {
        pds = ProgressDialog(this)
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.sign_in)
        binding = ReserverPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(localClassName)

        enableEdgeToEdge()

        // Récupérer le trajet depuis l'Intent
        trajet = intent.getSerializableExtra("trajet") as Trajet

        binding.reserverPlaceReserverPlace.setOnClickListener {
            reserverPlace(trajet)
        }
        chargerDonees(trajet)
    }

    private fun reserverPlace(trajet: Trajet) {
        currentUtilisateur = ObjetUtilisateur.loadUtilisateur(this)
        // Vérifie si l'utilisateur est connecté
        if(currentUtilisateur.uid == null) {
            currentUser?.uid?.let { uid ->
                utilisateurService.trouverUtilisateur(uid) { utilisateur ->
                    runOnUiThread {
                        if (utilisateur != null) {
                            ObjetUtilisateur.saveUtilisateur(this, utilisateur)
                            currentUtilisateur = utilisateur
                        } else {
                            Log.e("HomeFragment", "Utilisateur non trouvé")
                        }
                    }
                }
            } ?: Log.e("HomeFragment", "Utilisateur non trouvé")
        }
        var trouver = false

        if (trajet.listIdPassagerReservation.contains(currentUtilisateur.uid.toString()) || trajet.listIdPassager.contains(currentUtilisateur.uid.toString())) {
            trouver = true
        }

        if (!trouver){
            trajet.listIdPassagerReservation.add(currentUtilisateur.uid.toString())
            trajetService.modifierTrajet(trajet)
            Toast.makeText(this, "Réservation effectuée", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Déjà Réservé", Toast.LENGTH_SHORT).show()
        }
    }

    private fun chargerDonees(trajet: Trajet) {
        conducteurService.trouverConducteur(trajet.idConducteur) { conducteur ->
            if (conducteur != null) {
                // Si le conducteur est trouvé
                println("Conducteur trouvé : ${conducteur.utilisateur?.nom}")
                binding.reserverPlaceDestinationLabel.text = trajet.destination
                binding.reserverPlaceHeureDepart.text = trajet.heureDepart
                binding.reserverPlaceLieuDepart.text = trajet.lieuDepart
                binding.reserverPlacePrix.text = trajet.prixParPassager.toString()
                // Afficher les étoiles en fonction de la note
                displayStars(conducteur.note ?: 0, binding.reserverPlaceStarContainer, this)
                binding.reserverPlaceNomChauffeur.text = conducteur.utilisateur?.nom
                binding.reserverPlaceNumeroChauffeur.text = conducteur.numero
                binding.reserverPlaceMatriculeChauffeur.text = conducteur.voiture
                binding.reserverPlaceChatIcon.setOnClickListener{
                    actionChat(conducteur)
                }
            } else {
                // Si le conducteur n'est pas trouvé
                println("Conducteur non trouvé")
            }
        }
    }

    private fun actionChat(conducteur : Conducteur) {
        currentUtilisateur = ObjetUtilisateur.loadUtilisateur(this)
        // Vérifie si l'utilisateur est connecté
        if(currentUtilisateur.uid == null) {
            currentUser?.uid?.let { uid ->
                utilisateurService.trouverUtilisateur(uid) { utilisateur ->
                    runOnUiThread {
                        if (utilisateur != null) {
                            ObjetUtilisateur.saveUtilisateur(this, utilisateur)
                            currentUtilisateur = utilisateur
                            creerEtTrouverChat(conducteur)
                        } else {
                            Log.e("HomeFragment", "Utilisateur non trouvé")
                        }
                    }
                }
            } ?: Log.e("HomeFragment", "Utilisateur non trouvé")
        }else {
            creerEtTrouverChat(conducteur)
        }
    }

    private fun creerEtTrouverChat(conducteur: Conducteur) {
        var trouver = false
        var chatsList : MutableList<Chat> = mutableListOf()
        chatService.listerChats { chats ->
            for (chat in chats) {
                chatsList.add(chat)
            }
        }
        for (chat in chatsList) {
            if ((chat.conducteurId == conducteur.utilisateur?.uid && chat.passagerId == currentUtilisateur.uid) || (chat.passagerId == conducteur.utilisateur?.uid && chat.conducteurId == currentUtilisateur.uid)){
                demarrerChat(chat)
                trouver = true
                break
            }
        }
        if (!trouver) {
            var chat = Chat().apply {
                passagerId = currentUtilisateur.uid.toString()
                conducteurId = conducteur.utilisateur?.uid.toString()
                id = "${conducteurId}_${passagerId}"
            }
            // Attendre un peu avant de récupérer le chat
            chatService.trouverChat("${conducteur.utilisateur?.uid.toString()}_${currentUtilisateur.uid.toString()}") { chatTrouve ->
                if (chatTrouve != null) {
                    demarrerChat(chatTrouve)
                } else {
                    chatService.ajouterChat(chat)
                    demarrerChat(chat)
                }
            }
        }
    }

    private fun demarrerChat(chat: Chat) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("CHAT_ID", "${chat.id}") // Génération de l'ID unique du chat
        }
        startActivity(intent)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        //startActivity(intent)
        super.onBackPressed()
        pds?.dismiss()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        pds?.dismiss()

    }

    private fun displayStars(note: Int, starContainer: LinearLayout, context: Context) {
        // Vider le LinearLayout avant d'ajouter les étoiles
        starContainer.removeAllViews()

        // Ajouter les étoiles selon la note
        for (i in 1..5) {
            val starImage = ImageView(context)
            if (i <= note) {
                starImage.setImageResource(R.drawable.star_plein) // Étoile remplie
            } else {
                starImage.setImageResource(R.drawable.star) // Étoile vide
            }
            starImage.layoutParams = LinearLayout.LayoutParams(20.dpToPx(context), 20.dpToPx(context)) // Ajuster la taille des étoiles
            starContainer.addView(starImage)
        }
    }

    // Extension pour convertir dp en pixels
    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}