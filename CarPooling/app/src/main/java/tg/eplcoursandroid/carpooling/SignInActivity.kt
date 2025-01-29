@file:Suppress("DEPRECATION")

package tg.eplcoursandroid.carpooling

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import tg.eplcoursandroid.carpooling.databinding.SignInBinding
import tg.eplcoursandroid.carpooling.models.Passager
import tg.eplcoursandroid.carpooling.models.Utilisateur
import tg.eplcoursandroid.carpooling.service.AuthService
import tg.eplcoursandroid.carpooling.service.ConducteurService
import tg.eplcoursandroid.carpooling.service.PassagerService
import tg.eplcoursandroid.carpooling.service.UtilisateurService

/*import android.app.ProgressDialog
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.chatmessenger.MainActivity
import com.example.chatmessenger.R
import com.example.chatmessenger.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException*/

class SignInActivity : AppCompatActivity() {

    /*private lateinit var goToSignUpActivity : TextView
    private lateinit var email : String
    private lateinit var password : String
    private lateinit var auth : FirebaseAuth
    private lateinit var progressDialogSignIn : ProgressDialog
    private lateinit var signinInBinding : SignInBinding*/

    lateinit var name: String
    lateinit var email: String
    lateinit var password: String
    lateinit private var fbauth: FirebaseAuth
    lateinit private var pds: ProgressDialog
    lateinit var binding : SignInBinding

    private val authService = AuthService()
    private val passagerService = PassagerService()
    private val utilisateurService = UtilisateurService()
    private val conducteurService = ConducteurService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.sign_in)
        binding = SignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(localClassName)
        fbauth = FirebaseAuth.getInstance()
        if (fbauth.currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
        }
        pds = ProgressDialog(this)
        binding.signInCreerCompte.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.signInConnexion.setOnClickListener {
            email = binding.signInEmail.text.toString()
            password = binding.signInMotDePasse.text.toString()
            if (binding.signInEmail.text.isEmpty()){
                Toast.makeText(this, "Entrer votre email", Toast.LENGTH_SHORT).show()
            }
            if (binding.signInMotDePasse.text.isEmpty()){
                Toast.makeText(this, "Enter votre mot de passe", Toast.LENGTH_SHORT).show()
            }
            if (binding.signInEmail.text.isNotEmpty() && binding.signInMotDePasse.text.isNotEmpty()){
                signIn(password, email)
            }
        }
    }
    private fun signIn(password: String, email: String) {
        pds.show()
        pds.setMessage("Connexion en cours")
        authService.connecter(email, password) { success, message ->
            if (success) {
                Log.d("Inscrire", "rahim connecte")
                pds.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Log.d("Inscrire", "rahim non connecte")
                pds.dismiss()
                Toast.makeText(applicationContext, "Données invalides", Toast.LENGTH_SHORT).show()
            }
        }

        /*fbauth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful){
                pds.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                pds.dismiss()
                Toast.makeText(applicationContext, "Données invalides", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {exception->
            when (exception){
                is FirebaseAuthInvalidCredentialsException ->{
                    Toast.makeText(applicationContext, "Données invalides", Toast.LENGTH_SHORT).show()
                }
                else-> {
                    // other exceptions
                    Toast.makeText(applicationContext, "Authentification échouée", Toast.LENGTH_SHORT).show()
                }
            }
        }*/
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        pds.dismiss()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        pds.dismiss()

    }

}