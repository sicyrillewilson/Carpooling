@file:Suppress("DEPRECATION")

package tg.eplcoursandroid.carpooling

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import tg.eplcoursandroid.carpooling.adapter.ChatAdapter
import tg.eplcoursandroid.carpooling.adapter.ChauffeurItemAdapter
import tg.eplcoursandroid.carpooling.adapter.ChauffeurReservationItemAdapter
import tg.eplcoursandroid.carpooling.adapter.ListReservationAdapter
import tg.eplcoursandroid.carpooling.database.ObjetConducteur
import tg.eplcoursandroid.carpooling.database.ObjetUtilisateur
import tg.eplcoursandroid.carpooling.databinding.DevenirChauffeurLayoutBinding
import tg.eplcoursandroid.carpooling.databinding.ListeReservationChauffeurBinding
import tg.eplcoursandroid.carpooling.databinding.NouveauTrajetLayoutBinding
import tg.eplcoursandroid.carpooling.databinding.ReservationChauffeurBinding
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

class ItemReservationActivity : AppCompatActivity() {

    lateinit var password: String
    lateinit private var fbauth: FirebaseAuth
    lateinit var binding : ListeReservationChauffeurBinding

    private val trajetService = TrajetService()
    private val authService = AuthService()
    private val utilisateurService = UtilisateurService()
    private val conducteurService = ConducteurService()
    private val currentUser = authService.getCurrentUser()
    private var currentConducteur = Conducteur()
    private var currentUtilisateur = Utilisateur()
    private var currentTrajet = Trajet()

    lateinit private var destination : EditText
    private lateinit var lieuDepart : EditText
    private lateinit var heureDepart : EditText
    private lateinit var prix : EditText


    private var listUtilisateurReservation : MutableList<Utilisateur> = mutableListOf()


    // Ajout de la variable pour suivre l'état du mot de passe
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListeReservationChauffeurBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(localClassName)

        //enableEdgeToEdge()

        binding.listeReservationChauffeurRetour.setOnClickListener {
            finish()
        }

        currentTrajet = intent.getSerializableExtra("trajet") as Trajet

        trajetService.trouverTrajet(currentTrajet.idTrajet){ trajet ->
            if (trajet != null){
                currentTrajet = trajet
            }
        }

        listUtilisateurReservation.clear()

        utilisateurService.listerUtilisateurs { utilisateurs ->
            if(utilisateurs.isNotEmpty()) {
                for (utilisateur in utilisateurs){
                    if(currentTrajet.listIdPassagerReservation.contains(utilisateur.uid)){
                        listUtilisateurReservation.add(utilisateur)
                    }
                }
                binding.listeReservationChauffeurRecyclerview.layoutManager = LinearLayoutManager(this)
                binding.listeReservationChauffeurRecyclerview.adapter = ListReservationAdapter(listUtilisateurReservation, currentTrajet)
                binding.listeReservationChauffeurRecyclerview.setHasFixedSize(true)
            }
        }
        listUtilisateurReservation.clear()

    }

    override fun onResume() {
        super.onResume()
        trajetService.trouverTrajet(currentTrajet.idTrajet){ trajet ->
            if (trajet != null){
                currentTrajet = trajet
            }
        }
        // Recharger les utilisateurs à chaque retour sur l'activité
        utilisateurService.listerUtilisateurs { utilisateurs ->
            listUtilisateurReservation.clear()
            for (utilisateur in utilisateurs) {
                if (currentTrajet.listIdPassagerReservation.contains(utilisateur.uid)) {
                    listUtilisateurReservation.add(utilisateur)
                }
            }
            binding.listeReservationChauffeurRecyclerview.adapter?.notifyDataSetChanged()
        }
    }

    /*override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    )
        }
    }*/
}