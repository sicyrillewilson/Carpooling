@file:Suppress("DEPRECATION")

package tg.eplcoursandroid.carpooling

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
import tg.eplcoursandroid.carpooling.databinding.SignInBinding
import tg.eplcoursandroid.carpooling.databinding.SignUpBinding
import tg.eplcoursandroid.carpooling.models.Passager
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
    lateinit var email: String
    lateinit var password: String
    lateinit var passwordConfirm: String


    private val authService = AuthService()
    private val passagerService = PassagerService()
    private val utilisateurService = UtilisateurService()
    private val conducteurService = ConducteurService()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.sign_in)
        binding = SignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(localClassName)
        auth = FirebaseAuth.getInstance()
        //firestore = FirebaseFirestore.getInstance()
        pd = ProgressDialog(this)
        binding.signUpSeConnecter.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
        binding.signUpValider.setOnClickListener {
            //name = binding.signUpNom.text.toString()
            email = binding.signUpEmail.text.toString()
            password = binding.signUpMotDePasse.text.toString()
            passwordConfirm = binding.signUpConfirmerMotDePasse.text.toString()
//            if (binding.signUpNom.text.isEmpty()){
//                Toast.makeText(this, "Enter Votre nom", Toast.LENGTH_SHORT).show()
//            }
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
            if (binding.signUpEmail.text.isNotEmpty() && binding.signUpMotDePasse.text.isNotEmpty() && binding.signUpConfirmerMotDePasse.text.isNotEmpty() && binding.signUpMotDePasse.text.toString() == binding.signUpConfirmerMotDePasse.text.toString()){
                //createAnAccount(name, password, email)
                createAnAccount(password, email)
            }
        }
    }

//    private fun createAnAccount(name: String, password: String, email: String) {
    private fun createAnAccount(password: String, email: String) {
    Toast.makeText(this, "Creation en cours", Toast.LENGTH_SHORT).show()
        pd.show()
        pd.setMessage("Enregistrement de l'utilisateur")

        // methode pour inscrire un salaud
        authService.inscrire("crepin@gmail.com", "fuckEPLforLife") { success, message ->
            Toast.makeText(this, "Methode inscrire executé", Toast.LENGTH_SHORT).show()
            if (success) {
                Log.d("Inscrire", "crepin inscrit")
                //pd.dismiss()
                Toast.makeText(this, "Succes inscription", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SignInActivity::class.java))
            } else {
                Log.d("Inscrire", "crepin non inscrit")
            }
        }

    /*
        val currentUser = authService.getCurrentUser()
        if (currentUser != null) {
            Log.d("ChatApp", "Utilisateur connecté : ${currentUser.email}")
        } else {
            Log.d("ChatApp", "Aucun utilisateur connecté.")
        }

        // creation d'un utilisateur qui est celui actuellement connecte
        val utilisateur = Utilisateur(currentUser!!.uid, currentUser.email, currentUser.displayName)
        utilisateurService.ajouterUtilisateur(utilisateur,onSuccess = {
            Log.d("MainActivity", "Utilisateur ajouté avec succès !")
        }, onFailure = { exception ->
            Log.e("MainActivity", "Erreur lors de l'ajout de l'utilisateur : ${exception.message}")
        })

        // creation d'un passager
        val passager = Passager(utilisateur, "crepin")
        passagerService.ajouterPassager(passager,onSuccess = {
            Log.d("MainActivity", "Passager ajouté avec succès !")
        }, onFailure = { exception ->
            Log.e("MainActivity", "Erreur lors de l'ajout du passager : ${exception.message}")
        })
*/
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
}