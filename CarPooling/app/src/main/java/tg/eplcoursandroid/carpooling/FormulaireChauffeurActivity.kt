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
import tg.eplcoursandroid.carpooling.databinding.SignInBinding
import tg.eplcoursandroid.carpooling.models.Conducteur
import tg.eplcoursandroid.carpooling.models.Passager
import tg.eplcoursandroid.carpooling.models.Utilisateur
import tg.eplcoursandroid.carpooling.service.AuthService
import tg.eplcoursandroid.carpooling.service.ConducteurService
import tg.eplcoursandroid.carpooling.service.PassagerService
import tg.eplcoursandroid.carpooling.service.UtilisateurService

class FormulaireChauffeurActivity : AppCompatActivity() {

    lateinit var password: String
    lateinit private var fbauth: FirebaseAuth
    lateinit private var pds: ProgressDialog
    lateinit var binding : DevenirChauffeurLayoutBinding

    private val authService = AuthService()
    private val passagerService = PassagerService()
    private val utilisateurService = UtilisateurService()
    private val conducteurService = ConducteurService()

    lateinit private var numero : EditText
    private lateinit var matricule : EditText
    private lateinit var annuler : Button
    private lateinit var creer : Button



    // Ajout de la variable pour suivre l'état du mot de passe
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.sign_in)

        //enableEdgeToEdge()

        pds = ProgressDialog(this)

        binding = DevenirChauffeurLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(localClassName)

        binding.devenirChauffeurRetour.setOnClickListener {
            //finish()
        }

        binding.devenirChauffeurAnnuler.setOnClickListener {
            methodeAnnuler()
        }
        binding.devenirChauffeurCreer.setOnClickListener {
            methodeCreer()
        }

    }

    private fun methodeCreer() {
        val currentUser = authService.getCurrentUser()

        val numero = binding.devenirChauffeurNumero.text.toString()
        val matricule = binding.devenirChauffeurMatricule.text.toString()

        // Vérification des champs vides
        if (numero.isEmpty() || numero.toDouble()<10000000) {
            Toast.makeText(this, "Veuillez entrer un numéro convenable", Toast.LENGTH_SHORT).show()
            return
        }

        if (matricule.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer votre matricule", Toast.LENGTH_SHORT).show()
            return
        }

        var user: Utilisateur = Utilisateur("", "", "", "")

        currentUser?.uid?.let { uid ->
            utilisateurService.trouverUtilisateur(uid) { utilisateur ->
                runOnUiThread {
                    if (utilisateur != null) {
                        user = utilisateur
                        Toast.makeText(this, "${user.nom}, vous êtes maintenant un chauffeur", Toast.LENGTH_SHORT).show()

                        val conducteur = Conducteur(user, numero, 0, matricule)
                        conducteurService.ajouterConducteur(conducteur)
                        ObjetConducteur.saveConducteur(this, conducteur)
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()

                    } else {
                        Toast.makeText(this, "Échec de la création", Toast.LENGTH_SHORT).show()
                        Log.e("FormulaireChauffeur", "Utilisateur non trouvé")
                    }
                }
            }
        } ?: Log.e("FormulaireChauffeur", "Utilisateur non trouvé")
    }

    private fun methodeAnnuler() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        pds?.dismiss()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        pds?.dismiss()

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