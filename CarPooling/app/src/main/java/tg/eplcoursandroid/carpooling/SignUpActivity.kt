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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import tg.eplcoursandroid.carpooling.database.ObjetUtilisateur
import tg.eplcoursandroid.carpooling.databinding.SignUpBinding
import tg.eplcoursandroid.carpooling.models.Utilisateur
import tg.eplcoursandroid.carpooling.service.AuthService
import tg.eplcoursandroid.carpooling.service.ConducteurService
import tg.eplcoursandroid.carpooling.service.PassagerService
import tg.eplcoursandroid.carpooling.service.UtilisateurService

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: SignUpBinding
    lateinit var pd : ProgressDialog
    lateinit var auth : FirebaseAuth
    lateinit var nom: String
    lateinit var email: String
    lateinit var password: String
    lateinit var passwordConfirm: String


    private val authService = AuthService()
    private val utilisateurService = UtilisateurService()

    // Ajout de la variable pour suivre l'état du mot de passe
    private var isPasswordVisible = true


    lateinit private var fbauth: FirebaseAuth

    // Variables pour Google Sign-In
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.sign_in)
        binding = SignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(localClassName)

        enableEdgeToEdge()

        fbauth = FirebaseAuth.getInstance()
        if (fbauth.currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        auth = FirebaseAuth.getInstance()
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

        // Configuration de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.signUpContinueAvecGoogle.setOnClickListener {
            signUpWithGoogle()
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


    private fun signUpWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Échec de l'authentification Google", Toast.LENGTH_SHORT).show()
                Log.w("SignInActivity", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        fbauth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = fbauth.currentUser?.uid ?: return@addOnCompleteListener
                    val email = account.email ?: "email_inconnu@example.com"
                    val nom = account.displayName ?: "Nom inconnu"

                    // Mise à jour ou ajout de l'utilisateur
                    val utilisateur = Utilisateur(uid, email, nom, "")
                    utilisateurService.ajouterUtilisateur(utilisateur, onSuccess = {
                        Log.d("SignInActivity", "Utilisateur mis à jour ou ajouté avec succès : $nom ($email)")
                    }, onFailure = { exception ->
                        Log.e("SignInActivity", "Erreur lors de la mise à jour de l'utilisateur : ${exception.message}")
                    })

                    // Sauvegarder l'utilisateur localement
                    ObjetUtilisateur.saveUtilisateur(this, utilisateur)

                    // Rediriger vers MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentification avec Google échouée", Toast.LENGTH_SHORT).show()
                }
            }
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