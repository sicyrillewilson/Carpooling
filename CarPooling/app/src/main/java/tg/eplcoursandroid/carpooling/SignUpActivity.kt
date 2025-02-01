@file:Suppress("DEPRECATION")

package tg.eplcoursandroid.carpooling

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import tg.eplcoursandroid.carpooling.database.ObjetUtilisateur
//import com.google.firebase.firestore.FirebaseFirestore
import tg.eplcoursandroid.carpooling.databinding.SignInBinding
import tg.eplcoursandroid.carpooling.databinding.SignUpBinding
import tg.eplcoursandroid.carpooling.models.Passager
import tg.eplcoursandroid.carpooling.models.Trajet
import tg.eplcoursandroid.carpooling.models.Utilisateur
import tg.eplcoursandroid.carpooling.service.AuthService
import tg.eplcoursandroid.carpooling.service.ConducteurService
import tg.eplcoursandroid.carpooling.service.PassagerService
import tg.eplcoursandroid.carpooling.service.UtilisateurService

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: SignUpBinding
    lateinit var pd : ProgressDialog
    lateinit var auth : FirebaseAuth
    //lateinit var firestore : FirebaseFirestore
    //lateinit var name: String
    lateinit var nom: String
    lateinit var email: String
    lateinit var password: String
    lateinit var passwordConfirm: String


    private val authService = AuthService()
    private val passagerService = PassagerService()
    private val utilisateurService = UtilisateurService()
    private val conducteurService = ConducteurService()

    // Ajout de la variable pour suivre l'état du mot de passe
    private var isPasswordVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.sign_in)
        binding = SignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(localClassName)

        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()
        //firestore = FirebaseFirestore.getInstance()
        pd = ProgressDialog(this)

        togglePasswordVisibility()

        // Clic sur l'icône pour afficher/masquer le mot de passe
        binding.passwordToggle.setOnClickListener {
            togglePasswordVisibility()
            // Mettre à jour l'état de la visibilité
            isPasswordVisible = !isPasswordVisible
            // Re-donner le focus à l'EditText pour que le texte apparaisse immédiatement
            binding.signUpMotDePasse.setSelection(binding.signUpMotDePasse.text.length)
        }

        binding.passwordToggle2.setOnClickListener {
            togglePasswordVisibility()
            // Mettre à jour l'état de la visibilité
            isPasswordVisible = !isPasswordVisible
            // Re-donner le focus à l'EditText pour que le texte apparaisse immédiatement
            binding.signUpConfirmerMotDePasse.setSelection(binding.signUpConfirmerMotDePasse.text.length)
        }

        binding.signUpSeConnecter.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
        binding.signUpValider.setOnClickListener {
            //name = binding.signUpNom.text.toString()
            nom = binding.signUpNom.text.toString()
            email = binding.signUpEmail.text.toString()
            password = binding.signUpMotDePasse.text.toString()
            passwordConfirm = binding.signUpConfirmerMotDePasse.text.toString()
//            if (binding.signUpNom.text.isEmpty()){
//                Toast.makeText(this, "Enter Votre nom", Toast.LENGTH_SHORT).show()
//            }
            if (binding.signUpNom.text.isEmpty()){
                Toast.makeText(this, "Enter votre nom", Toast.LENGTH_SHORT).show()
            }
            if (binding.signUpEmail.text.isEmpty()){
                Toast.makeText(this, "Enter votre email", Toast.LENGTH_SHORT).show()
            }
            if (binding.signUpMotDePasse.text.isEmpty()){
                Toast.makeText(this, "Enter votre mot de pasase", Toast.LENGTH_SHORT).show()
            }
            if (binding.signUpConfirmerMotDePasse.text.isEmpty() || binding.signUpMotDePasse.text.toString() != binding.signUpConfirmerMotDePasse.text.toString()) {
                Toast.makeText(this, "Veillez confirmer votre mot de passe", Toast.LENGTH_SHORT).show()
            }
            //if (binding.signUpNom.text.isNotEmpty() && binding.signUpEmail.text.isNotEmpty() && binding.signUpMotDePasse.text.isNotEmpty()){
            if (binding.signUpNom.text.isNotEmpty() && binding.signUpEmail.text.isNotEmpty() && binding.signUpMotDePasse.text.isNotEmpty() && binding.signUpConfirmerMotDePasse.text.isNotEmpty() && binding.signUpMotDePasse.text.toString() == binding.signUpConfirmerMotDePasse.text.toString()){
                //createAnAccount(name, password, email)
                createAnAccount(password, email, nom)
            }
        }
    }

    private fun togglePasswordVisibility() {
        val typeface = binding.signUpMotDePasse.typeface // Sauvegarde la police

        if (isPasswordVisible) {
            // Masquer le mot de passe
            binding.signUpMotDePasse.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.passwordToggle.setImageResource(R.drawable.eye)

            binding.signUpConfirmerMotDePasse.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.passwordToggle2.setImageResource(R.drawable.eye)
        } else {
            // Afficher le mot de passe
            binding.signUpMotDePasse.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.passwordToggle.setImageResource(R.drawable.hidden)

            binding.signUpConfirmerMotDePasse.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.passwordToggle2.setImageResource(R.drawable.hidden)
        }

        // Réappliquer la police
        binding.signUpMotDePasse.typeface = typeface
        binding.signUpConfirmerMotDePasse.typeface = typeface
    }

//    private fun createAnAccount(name: String, password: String, email: String) {
    private fun createAnAccount(password: String, email: String, nom: String) {
        pd.show()
        pd.setMessage("Enregistrement de l'utilisateur")

        // methode pour inscrire un salaud
        authService.inscrire(email, password) { success, message ->
            if (success) {
                pd.dismiss()
                Log.d("Inscrire", "${email} inscrit")
                val currentUser = authService.getCurrentUser()
                if (currentUser != null) {
                    Log.d("SignUpSignUp", "Utilisateur connecté : ${currentUser.email}")
                } else {
                    Log.d("SignUpSignUp", "Aucun utilisateur connecté.")
                }

                // creation d'un utilisateur qui est celui actuellement connecte
//                val utilisateur = Utilisateur(currentUser!!.uid, currentUser.email, nom, listOf(
//                    Trajet("", "", "", "", "", .0, 0, "")
//                ),  "")
                val utilisateur = Utilisateur(currentUser!!.uid, currentUser.email, nom,  "")
                utilisateurService.ajouterUtilisateur(utilisateur,onSuccess = {
                    Log.d("SignUpSignUp", "Utilisateur ajouté avec succès !")
                }, onFailure = { exception ->
                    Log.e("SignUpSignUp", "Erreur lors de l'ajout de l'utilisateur : ${exception.message}")
                })

                ObjetUtilisateur.saveUtilisateur(this, utilisateur)

                /*// creation d'un passager
                val passager = Passager(utilisateur, nom)
                passagerService.ajouterPassager(passager,onSuccess = {
                    Log.d("MainActivity", "Passager ajouté avec succès !")
                }, onFailure = { exception ->
                    Log.e("MainActivity", "Erreur lors de l'ajout du passager : ${exception.message}")
                })*/
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            } else {
                pd.dismiss()
                Log.e("SignUpSignUp", "Erreur : $message")
                Log.d("SignUpSignUp", "${email} non inscrit")
            }
        }

        /*auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {task->
            if (task.isSuccessful){
                val user = auth.currentUser
                //val dataHashMap = hashMapOf("userid" to user!!.uid!!, "username" to name, "useremail" to email, "status" to "default",
                val dataHashMap = hashMapOf("userid" to user!!.uid!!, "useremail" to email, "status" to "default",
                    "imageUrl" to "https://www.pngarts.com/files/6/User-Avatar-in-Suit-PNG.png")
                firestore.collection("Users").document(user.uid).set(dataHashMap)
                pd.dismiss()
                startActivity(Intent(this, SignInActivity::class.java))
            }
        }*/
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    )
        }
    }
}