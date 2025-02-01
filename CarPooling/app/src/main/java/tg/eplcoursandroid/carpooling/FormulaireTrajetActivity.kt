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
    lateinit var binding : NouveauTrajetLayoutBinding

    private val trajetService = TrajetService()
    private val authService = AuthService()
    private val utilisateurService = UtilisateurService()
    private val conducteurService = ConducteurService()

    lateinit private var destination : EditText
    private lateinit var lieuDepart : EditText
    private lateinit var heureDepart : EditText
    private lateinit var places : EditText
    private lateinit var prix : EditText



    // Ajout de la variable pour suivre l'état du mot de passe
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NouveauTrajetLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(localClassName)

        //enableEdgeToEdge()

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
        val destination = binding.nouveauTrajetDestination.text.toString()
        val lieuDepart = binding.nouveauTrajetLieuDepart.text.toString()
        val heureDepart = binding.nouveauTrajetHeureDepart.text.toString()
        val places = binding.nouveauTrajetPlaces.text.toString()
        val prix = binding.nouveauTrajetPrix.text.toString()

        // Vérification des champs vides
        if (destination.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer une destination", Toast.LENGTH_SHORT).show()
            return
        }
        if (lieuDepart.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer un lieu de départ", Toast.LENGTH_SHORT).show()
            return
        }
        if (heureDepart.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer une heure de départ", Toast.LENGTH_SHORT).show()
            return
        }
        if (places.isEmpty() || places.toInt()<=0) {
            Toast.makeText(this, "Veuillez entrer un nombre de places disponibles convenable", Toast.LENGTH_SHORT).show()
            return
        }
        if (prix.isEmpty() || prix.toDouble()<=0 || prix.toDouble()>=100) {
            Toast.makeText(this, "Veuillez entrer un prix convenable par passager", Toast.LENGTH_SHORT).show()
            return
        }

        val trajet = Trajet().apply {
            this.destination = destination
            this.lieuDepart = lieuDepart
            this.heureDepart = heureDepart
            this.places = places
            this.prixParPassager = prix.toDouble()
            this.idConducteur = ObjetConducteur.loadConducteur(this@FormulaireTrajetActivity).utilisateur?.uid.toString()
            this.listIdPassager.add(idConducteur)
        }

        trajetService.ajouterTrajet(trajet)

        // Réinitialiser les champs après validation
        binding.nouveauTrajetDestination.setText("")
        binding.nouveauTrajetLieuDepart.setText("")
        binding.nouveauTrajetHeureDepart.setText("")
        binding.nouveauTrajetPlaces.setText("")
        binding.nouveauTrajetPrix.setText("")
    }

    private fun methodeAnnuler() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
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