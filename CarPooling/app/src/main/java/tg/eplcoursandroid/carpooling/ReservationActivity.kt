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
import tg.eplcoursandroid.carpooling.database.ObjetConducteur
import tg.eplcoursandroid.carpooling.databinding.DevenirChauffeurLayoutBinding
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

class ReservationActivity : AppCompatActivity() {

    lateinit var password: String
    lateinit private var fbauth: FirebaseAuth
    lateinit var binding : ReservationChauffeurBinding

    private val trajetService = TrajetService()
    private val authService = AuthService()
    private val utilisateurService = UtilisateurService()
    private val conducteurService = ConducteurService()
    private val currentUser = authService.getCurrentUser()
    private var currentConducteur = Conducteur()

    private var trajets: MutableList<Trajet> = mutableListOf()
    private var trajetsConducteur: MutableList<Trajet> = mutableListOf()
    private var trajetsConducteurReservation: MutableList<Trajet> = mutableListOf()


    // Ajout de la variable pour suivre l'état du mot de passe
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ReservationChauffeurBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(localClassName)

        //enableEdgeToEdge()

        binding.reservationChauffeurRetour.setOnClickListener {
            finish()
        }

        currentConducteur = intent.getSerializableExtra("conducteur") as Conducteur


        trajetService.listerTrajets { traj ->
            if (traj != null) {
                trajets = traj.toMutableList()

                for (trajet in trajets) {
                    if (trajet.idConducteur == currentConducteur.utilisateur?.uid) {
                        trajetsConducteur.add(trajet)
                    }
                }

                for (trajet in trajetsConducteur) {
                    if(trajet.listIdPassagerReservation.isNotEmpty()){
                        trajetsConducteurReservation.add(trajet)
                    }
                }


                binding.reservationChauffeurRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.reservationChauffeurRecyclerview.adapter = ChauffeurReservationItemAdapter(trajetsConducteurReservation) { trajet ->
                    val intent = Intent(this, ItemReservationActivity::class.java)
                    intent.putExtra("trajet", trajet)
                    startActivity(intent)
                }
                binding.reservationChauffeurRecyclerview.setHasFixedSize(true)

                //trajetsConducteurReservation.clear()

            } else {
                println("Trajets non trouvé")
            }
        }

    }

    override fun onResume() {
        super.onResume()

        trajetService.listerTrajets { traj ->
            if (traj != null) {
                trajets.clear() // Vider la liste avant de la recharger
                trajetsConducteur.clear()
                trajetsConducteurReservation.clear()

                trajets.addAll(traj)

                for (trajet in trajets) {
                    if (trajet.idConducteur == currentConducteur.utilisateur?.uid) {
                        trajetsConducteur.add(trajet)
                    }
                }

                for (trajet in trajetsConducteur) {
                    if(trajet.listIdPassagerReservation.isNotEmpty()){
                        trajetsConducteurReservation.add(trajet)
                    }
                }

                binding.reservationChauffeurRecyclerview.adapter?.notifyDataSetChanged()
            } else {
                println("Trajets non trouvés")
            }
        }
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