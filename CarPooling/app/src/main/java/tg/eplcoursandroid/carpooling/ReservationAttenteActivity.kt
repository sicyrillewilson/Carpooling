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
import tg.eplcoursandroid.carpooling.adapter.ChauffeurItemAdapter
import tg.eplcoursandroid.carpooling.adapter.ChauffeurReservationItemAdapter
import tg.eplcoursandroid.carpooling.adapter.HistoriqueAdapter
import tg.eplcoursandroid.carpooling.database.ObjetConducteur
import tg.eplcoursandroid.carpooling.database.ObjetUtilisateur
import tg.eplcoursandroid.carpooling.databinding.DevenirChauffeurLayoutBinding
import tg.eplcoursandroid.carpooling.databinding.FragmentHistoriqueAttenteBinding
import tg.eplcoursandroid.carpooling.databinding.NouveauTrajetLayoutBinding
import tg.eplcoursandroid.carpooling.databinding.ReservationChauffeurBinding
import tg.eplcoursandroid.carpooling.databinding.SignInBinding
import tg.eplcoursandroid.carpooling.models.Conducteur
import tg.eplcoursandroid.carpooling.models.Passager
import tg.eplcoursandroid.carpooling.models.Trajet
import tg.eplcoursandroid.carpooling.models.Utilisateur
import tg.eplcoursandroid.carpooling.service.AuthService
import tg.eplcoursandroid.carpooling.service.ConducteurService
import tg.eplcoursandroid.carpooling.service.PassagerService
import tg.eplcoursandroid.carpooling.service.TrajetService
import tg.eplcoursandroid.carpooling.service.UtilisateurService

class ReservationAttenteActivity : AppCompatActivity() {

    lateinit var password: String
    lateinit private var fbauth: FirebaseAuth
    lateinit var binding : FragmentHistoriqueAttenteBinding

    private val trajetService = TrajetService()
    private val authService = AuthService()
    private val utilisateurService = UtilisateurService()
    private val conducteurService = ConducteurService()
    private val currentUser = authService.getCurrentUser()
    private var currentConducteur = Conducteur()

    private var trajetsReservation: MutableList<Trajet> = mutableListOf()


    // Ajout de la variable pour suivre l'état du mot de passe
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentHistoriqueAttenteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(localClassName)

        //enableEdgeToEdge()

        binding.fragmentHistoriqueAttenteRetour.setOnClickListener {
            finish()
        }

        chargerHistorique()
    }

    private fun chargerHistorique() {
        trajetsReservation = (intent.getSerializableExtra("trajets") as? ArrayList<Trajet>)?.toMutableList() ?: mutableListOf()

        binding.fragmentHistoriqueAttenteRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.fragmentHistoriqueAttenteRecyclerview.adapter = HistoriqueAdapter(trajetsReservation)
        binding.fragmentHistoriqueAttenteRecyclerview.setHasFixedSize(true)
        binding.fragmentHistoriqueAttenteRecyclerview.adapter?.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        chargerHistorique()
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