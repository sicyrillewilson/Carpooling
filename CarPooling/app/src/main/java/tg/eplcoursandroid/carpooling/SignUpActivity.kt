@file:Suppress("DEPRECATION")

package tg.eplcoursandroid.carpooling

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import tg.eplcoursandroid.carpooling.databinding.SignInBinding
import tg.eplcoursandroid.carpooling.databinding.SignUpBinding

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: SignUpBinding
    lateinit var pd : ProgressDialog
    lateinit var auth : FirebaseAuth
    lateinit var firestore : FirebaseFirestore
    lateinit var name: String
    lateinit var email: String
    lateinit var password: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.sign_in)
        binding = SignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(localClassName)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        pd = ProgressDialog(this)
        binding.signUpSeConnecter.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
        binding.signUpValider.setOnClickListener {
            name = binding.signUpNom.text.toString()
            email = binding.signUpEmail.text.toString()
            password = binding.signUpMotDePasse.text.toString()
            if (binding.signUpNom.text.isEmpty()){
                Toast.makeText(this, "Enter Votre nom", Toast.LENGTH_SHORT).show()
            }
            if (binding.signUpEmail.text.isEmpty()){
                Toast.makeText(this, "Enter votre email", Toast.LENGTH_SHORT).show()
            }
            if (binding.signUpMotDePasse.text.isEmpty()){
                Toast.makeText(this, "Enter votre mot de pasase", Toast.LENGTH_SHORT).show()
            }
            if (binding.signUpNom.text.isNotEmpty() && binding.signUpEmail.text.isNotEmpty() && binding.signUpMotDePasse.text.isNotEmpty()){
                createAnAccount(name, password, email)
            }
        }
    }

    private fun createAnAccount(name: String, password: String, email: String) {
        pd.show()
        pd.setMessage("Enregistrement de l'utilisateur")
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {task->
            if (task.isSuccessful){
                val user = auth.currentUser
                val dataHashMap = hashMapOf("userid" to user!!.uid!!, "username" to name, "useremail" to email, "status" to "default",
                    "imageUrl" to "https://www.pngarts.com/files/6/User-Avatar-in-Suit-PNG.png")
                firestore.collection("Users").document(user.uid).set(dataHashMap)
                pd.dismiss()
                startActivity(Intent(this, SignInActivity::class.java))
            }
        }
    }
}