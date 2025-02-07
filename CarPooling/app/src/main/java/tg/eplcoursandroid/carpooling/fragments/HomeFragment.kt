package tg.eplcoursandroid.carpooling.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import tg.eplcoursandroid.carpooling.R
import tg.eplcoursandroid.carpooling.ReserverPlaceActivity
import tg.eplcoursandroid.carpooling.SignInActivity
import tg.eplcoursandroid.carpooling.adapter.Trajet1Adapter
import tg.eplcoursandroid.carpooling.adapter.Trajet2Adapter
import tg.eplcoursandroid.carpooling.database.ObjetChats
import tg.eplcoursandroid.carpooling.database.ObjetConducteur
import tg.eplcoursandroid.carpooling.database.ObjetUtilisateur
import tg.eplcoursandroid.carpooling.databinding.FragmentHomeBinding
import tg.eplcoursandroid.carpooling.models.Conducteur
import tg.eplcoursandroid.carpooling.models.Trajet
import tg.eplcoursandroid.carpooling.models.Utilisateur
import tg.eplcoursandroid.carpooling.service.AuthService
import tg.eplcoursandroid.carpooling.service.ConducteurService
import tg.eplcoursandroid.carpooling.service.TrajetService
import tg.eplcoursandroid.carpooling.service.UtilisateurService

class HomeFragment : Fragment() {

    val auth = FirebaseAuth.getInstance()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val authService = AuthService()
    private val utilisateurService = UtilisateurService()
    private val currentUser = authService.getCurrentUser()
    private var currentUtilisateur = Utilisateur()

    private val trajetService = TrajetService()
    private var currentConducteur = Conducteur()
    private val conducteurService = ConducteurService()
    private var trajetsDisponibles: MutableList<Trajet> = mutableListOf()
    private var destinations: MutableList<String> = mutableListOf()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var trajet1Adapter: Trajet1Adapter
    private lateinit var trajet2Adapter: Trajet2Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUtilisateur = ObjetUtilisateur.loadUtilisateur(requireContext())
        // Vérifie si l'utilisateur est connecté
        if(currentUtilisateur.uid == null) {
            currentUser?.uid?.let { uid ->
                utilisateurService.trouverUtilisateur(uid) { utilisateur ->
                    requireActivity().runOnUiThread {
                        if (utilisateur != null) {
                            ObjetUtilisateur.saveUtilisateur(requireContext(), utilisateur)
                            currentUtilisateur = utilisateur
                            binding.homeNom.text = utilisateur.nom ?: "Utilisateur inconnu"
                            binding.homeEmail.text = utilisateur.email ?: "@emailNonAjouté"
                        } else {
                            Log.e("HomeFragment", "Utilisateur non trouvé")
                        }
                    }
                }
            } ?: Log.e("HomeFragment", "Utilisateur non trouvé")
        } else {
            binding.homeNom.text = currentUtilisateur.nom ?: "Utilisateur inconnu"
            binding.homeEmail.text = currentUtilisateur.email ?: "@emailNonAjouté"
        }

        binding.homeDeconnexion.setOnClickListener {
            logout()
        }

        chargerTrajets()
        setupRecherche()

        // Vérifie si l'utilisateur est un conducteur
        currentConducteur = ObjetConducteur.loadConducteur(requireContext())

        if (currentConducteur.utilisateur == null) {
            currentUser?.uid?.let { uid ->
                conducteurService.trouverConducteur(uid) { conducteur ->
                    requireActivity().runOnUiThread {
                        if (conducteur != null) {
                            currentConducteur = conducteur
                            ObjetConducteur.saveConducteur(requireContext(), conducteur)
                        } else {
                            Log.e("ChauffeurFragment", "Conducteur non trouvé 1")
                        }
                    }
                }
            } ?: Log.e("ChauffeurFragment", "Conducteur non trouvé 2")
        }
    }

    fun logout() {
        ObjetUtilisateur.saveUtilisateur(requireContext(), Utilisateur())
        ObjetConducteur.saveConducteur(requireContext(), Conducteur())
        ObjetChats.saveChats(requireContext(), listOf())
        // Déconnexion de Firebase
        auth.signOut()

        // Rediriger l'utilisateur vers l'écran de connexion
        val intent = Intent(requireContext(), SignInActivity::class.java) // Remplace par ton activité de connexion
        startActivity(intent)

        // Fermer l'activité actuelle (optionnel)
        requireActivity().finish() // Utilisation de requireActivity() pour accéder à la méthode finish()
    }

    private fun chargerTrajets() {
        trajetService.listerTrajets { trajets ->
            if (trajets != null) {
                trajetsDisponibles.clear()
                destinations.clear()

                /*for (trajet in trajets) {
                    if (trajet.places?.toInt()!! > 0) {
                        trajetsDisponibles.add(trajet)
                        trajet.destination?.let { dest ->
                            if (!destinations.contains(dest)) {
                                destinations.add(dest)  // Éviter les doublons
                            }
                        }
                    }
                }*/

                for (trajet in trajets) {
                    val places = trajet.places?.toIntOrNull()
                    if (places != null && places > 0) {
                        trajetsDisponibles.add(trajet)
                        trajet.destination?.let { dest ->
                            if (!destinations.contains(dest)) {
                                destinations.add(dest)  // Éviter les doublons
                            }
                        }
                    }
                }

                requireActivity().runOnUiThread {
                    mettreAJourRecyclerView()
                    adapter.notifyDataSetChanged() // Actualiser les suggestions
                }
            } else {
                Log.e("HomeFragment", "Aucun trajet trouvé")
            }
        }
    }

    private fun setupRecherche() {
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, destinations)
        binding.homeRecherche.setAdapter(adapter)

        binding.homeRecherche.setOnItemClickListener { _, _, position, _ ->
            val destinationSelectionnee = adapter.getItem(position) ?: return@setOnItemClickListener
            filtrerTrajets(destinationSelectionnee)
        }

        binding.homeRecherche.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s) // Mettre à jour la liste déroulante
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    /*private fun filtrerTrajets(destination: String) {
        val trajetsFiltres = trajetsDisponibles.filter { trajet ->
            trajet.destination.equals(destination, ignoreCase = true)
        }
        requireActivity().runOnUiThread {
            trajet1Adapter.updateData(trajetsFiltres)
            trajet2Adapter.updateData(emptyList()) // Pas besoin de la deuxième liste ici
        }
    }*/

    private fun filtrerTrajets(destination: String) {
        val trajetsFiltres = if (destination.isEmpty()) {
            trajetsDisponibles  // Si l'entrée est vide, afficher tous les trajets
        } else {
            trajetsDisponibles.filter { trajet ->
                trajet.destination.contains(destination, ignoreCase = true)  // Recherche partielle
            }
        }

        requireActivity().runOnUiThread {
            trajet1Adapter.updateData(trajetsFiltres)
            trajet2Adapter.updateData(emptyList())  // Effacer la deuxième liste si besoin
        }
    }

    private fun mettreAJourRecyclerView() {
        // Crée une copie de la liste originale pour éviter la modification concurrente
        val trajetsDisponiblesCopy = ArrayList(trajetsDisponibles)

        val midIndex = trajetsDisponiblesCopy.size / 2

        // Diviser la liste copiée
        val trajet1 = trajetsDisponiblesCopy.subList(0, midIndex)
        val trajet2 = trajetsDisponiblesCopy.subList(midIndex, trajetsDisponiblesCopy.size)

        // Mettre à jour les adaptateurs avec les nouvelles sous-listes
        trajet1Adapter = Trajet1Adapter(trajet1) { onTrajetClicked(it) }
        trajet2Adapter = Trajet2Adapter(trajet2) { onTrajetClicked(it) }

        binding.homeRecyclerTrajet1.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.homeRecyclerTrajet1.adapter = trajet1Adapter
        binding.homeRecyclerTrajet1.setHasFixedSize(true)
        trajet1Adapter.notifyDataSetChanged()  // Notifie l'adaptateur

        binding.homeRecyclerTrajet2.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.homeRecyclerTrajet2.adapter = trajet2Adapter
        binding.homeRecyclerTrajet2.setHasFixedSize(true)
        trajet2Adapter.notifyDataSetChanged()  // Notifie l'adaptateur
    }


    private fun onTrajetClicked(trajectoire: Trajet) {
        val intent = Intent(requireContext(), ReserverPlaceActivity::class.java)
        intent.putExtra("trajet", trajectoire)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        chargerTrajets()
    }
}
