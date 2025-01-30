@file:Suppress("DEPRECATION")

package tg.eplcoursandroid.carpooling

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import tg.eplcoursandroid.carpooling.fragments.HomeFragment
import tg.eplcoursandroid.carpooling.fragments.MapsFragment
import tg.eplcoursandroid.carpooling.fragments.ChatFragment
import tg.eplcoursandroid.carpooling.fragments.HistoriqueFragment
import tg.eplcoursandroid.carpooling.fragments.ChauffeurFragment
import tg.eplcoursandroid.carpooling.models.Conducteur
import tg.eplcoursandroid.carpooling.service.AuthService
import tg.eplcoursandroid.carpooling.service.ConducteurService

class MainActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false // Variable pour gérer le double appui
    private var currentFragment: Fragment? = null // Pour suivre quel fragment est affiché

    private val authService = AuthService()
    private val conducteurService = ConducteurService()
    private val currentUser = authService.getCurrentUser()
    private var currentConducteur = Conducteur()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bouton_navigation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Définir l'action sur les éléments de navigation
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_chat -> {
                    loadFragment(ChatFragment())
                    true
                }
                R.id.nav_maps -> {
                    loadFragment(MapsFragment())
                    true
                }
                R.id.nav_chauffeurs -> {
                    loadFragment(ChauffeurFragment())
                    true
                }
                R.id.nav_historique -> {
                    loadFragment(HistoriqueFragment())
                    true
                }
                else -> false
            }
        }

        // Charger un fragment par défaut
        bottomNavigationView.selectedItemId = R.id.nav_home

    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        currentFragment = fragment
    }
    override fun onBackPressed() {
        if (currentFragment is HomeFragment) {
            // Si on est sur HomeFragment, gérer le double appui pour quitter l'application
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed() // Quitter l'application
                return
            }

            this.doubleBackToExitPressedOnce = true
            // Afficher un message à l'utilisateur
            Toast.makeText(this, "Appuyez à nouveau pour quitter", Toast.LENGTH_SHORT).show()

            // Remettre la variable à false après un délai
            Handler(Looper.getMainLooper()).postDelayed({
                doubleBackToExitPressedOnce = false
            }, 2000) // Le délai pour un deuxième appui est de 2 secondes
        } else {
            // Si on n'est pas dans HomeFragment, on retourne au HomeFragment
            loadFragment(HomeFragment())
            // Charger un fragment par défaut
            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bouton_navigation)
            bottomNavigationView.selectedItemId = R.id.nav_home
        }
    }
}
