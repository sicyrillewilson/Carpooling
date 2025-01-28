package tg.eplcoursandroid.carpooling

import android.os.Bundle
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

class MainActivity : AppCompatActivity() {
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
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_maps -> {
                    loadFragment(MapsFragment())
                    true
                }
                R.id.nav_chauffeurs -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_historique -> {
                    loadFragment(HomeFragment())
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
    }
}

/*class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var nGoogleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragment_maps_id) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        nGoogleMap = googleMap
    }
}*/
