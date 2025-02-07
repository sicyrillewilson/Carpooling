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
import tg.eplcoursandroid.carpooling.databinding.SignInBinding
import tg.eplcoursandroid.carpooling.models.Utilisateur
import tg.eplcoursandroid.carpooling.service.AuthService
import tg.eplcoursandroid.carpooling.service.ConducteurService
import tg.eplcoursandroid.carpooling.service.PassagerService
import tg.eplcoursandroid.carpooling.service.UtilisateurService


class SignInActivity : AppCompatActivity() {

    lateinit var name: String
    lateinit var email: String
    lateinit var password: String
    lateinit private var fbauth: FirebaseAuth
    lateinit private var pds: ProgressDialog
    lateinit var binding : SignInBinding

    private val authService = AuthService()
    private val utilisateurService = UtilisateurService()
    // Ajout de la variable pour suivre l'état du mot de passe
    private var isPasswordVisible = true

    // Variables pour Google Sign-In
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.sign_in)
        binding = SignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(localClassName)

        enableEdgeToEdge()

        fbauth = FirebaseAuth.getInstance()
        if (fbauth.currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        pds = ProgressDialog(this)

        togglePasswordVisibility()

        // Clic sur l'icône pour afficher/masquer le mot de passe
        binding.passwordToggle.setOnClickListener {
            togglePasswordVisibility()
            // Mettre à jour l'état de la visibilité
            isPasswordVisible = !isPasswordVisible
            // Re-donner le focus à l'EditText pour que le texte apparaisse immédiatement
            binding.signInMotDePasse.setSelection(binding.signInMotDePasse.text.length)
        }

        binding.signInCreerCompte.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
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
        // Configuration de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.signInContinueAvecGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun togglePasswordVisibility() {
        val typeface = binding.signInMotDePasse.typeface // Sauvegarde la police

        if (isPasswordVisible) {
            // Masquer le mot de passe
            binding.signInMotDePasse.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.passwordToggle.setImageResource(R.drawable.eye)

        } else {
            // Afficher le mot de passe
            binding.signInMotDePasse.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.passwordToggle.setImageResource(R.drawable.hidden)

        }

        // Réappliquer la police
        binding.signInMotDePasse.typeface = typeface
    }

    private fun signIn(password: String, email: String) {
        pds.show()
        pds.setMessage("Connexion en cours")
        authService.connecter(email, password) { success, message ->
            if (success) {
                Log.d("Inscrire", "rahim connecte")
                pds.dismiss()
                //startActivity(Intent(this, MainActivity::class.java))
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Log.d("Inscrire", "rahim non connecte")
                pds.dismiss()
                Toast.makeText(applicationContext, "Données invalides", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithGoogle() {
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