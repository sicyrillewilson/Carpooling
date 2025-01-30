@file:Suppress("DEPRECATION")

package tg.eplcoursandroid.carpooling

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import tg.eplcoursandroid.carpooling.database.ObjetConducteur
import tg.eplcoursandroid.carpooling.databinding.DevenirChauffeurLayoutBinding
import tg.eplcoursandroid.carpooling.databinding.NouveauTrajetLayoutBinding
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

class FormulaireTrajetActivity : AppCompatActivity() {

    lateinit var password: String
    lateinit private var fbauth: FirebaseAuth
    lateinit private var pds: ProgressDialog
    lateinit var binding : NouveauTrajetLayoutBinding

    private val trajetService = TrajetService()
    private val authService = AuthService()
    private val utilisateurService = UtilisateurService()
    private val conducteurService = ConducteurService()

    lateinit private var destination : EditText
    private lateinit var lieuDepart : EditText
    private lateinit var heureDepart : EditText
    private lateinit var prix : EditText



    // Ajout de la variable pour suivre l'état du mot de passe
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NouveauTrajetLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(localClassName)

        binding.nouveauTrajetRetour.setOnClickListener {
            finish()
        }

        binding.nouveauTrajetAnnuler.setOnClickListener {
            methodeAnnuler()
        }
        binding.nouveauTrajetValider.setOnClickListener {
            methodeValider()
        }

    }

    private fun methodeValider() {
        destination = findViewById(R.id.nouveau_trajet_destination)
        lieuDepart = findViewById(R.id.nouveau_trajet_lieu_depart)
        heureDepart = findViewById(R.id.nouveau_trajet_heure_depart)
        prix = findViewById(R.id.nouveau_trajet_prix)

        var trajet = Trajet()
        trajet.destination = destination.text.toString()
        trajet.lieuDepart = lieuDepart.text.toString()
        trajet.heureDepart = heureDepart.text.toString()
        trajet.prixParPassager = prix.text.toString().toDouble()
        trajet.idConducteur = ObjetConducteur.loadConducteur(this).utilisateur?.uid.toString()

        trajetService.ajouterTrajet(trajet)

        destination.setText("")
        lieuDepart.setText("")
        heureDepart.setText("")
        prix.setText("")
    }

    private fun methodeAnnuler() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}