package tg.eplcoursandroid.carpooling.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import tg.eplcoursandroid.carpooling.R
import tg.eplcoursandroid.carpooling.ReserverPlaceActivity
import tg.eplcoursandroid.carpooling.SignInActivity
import tg.eplcoursandroid.carpooling.adapter.Trajet1Adapter
import tg.eplcoursandroid.carpooling.adapter.Trajet2Adapter
import tg.eplcoursandroid.carpooling.database.ObjetUtilisateur
import tg.eplcoursandroid.carpooling.databinding.FragmentHomeBinding
import tg.eplcoursandroid.carpooling.models.Trajet
import tg.eplcoursandroid.carpooling.models.Utilisateur
import tg.eplcoursandroid.carpooling.service.AuthService
import tg.eplcoursandroid.carpooling.service.TrajetService
import tg.eplcoursandroid.carpooling.service.UtilisateurService

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val authService = AuthService()
    private val utilisateurService = UtilisateurService()
    private val currentUser = authService.getCurrentUser()
    private var currentUtilisateur = Utilisateur()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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

        var trajetService = TrajetService()
        var trajets: List<Trajet> = listOf()

        trajetService.listerTrajets { traj ->
            if (traj != null) {
                trajets = traj

                val midIndex = traj.size / 2

                val trajet1 = traj.subList(0, midIndex) // Première moitié
                val trajet2 = traj.subList(midIndex, traj.size) // Deuxième moitié

                binding.homeRecyclerTrajet1.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.homeRecyclerTrajet1.adapter = Trajet1Adapter(trajet1) {
                        trajectoire -> onTrajetClicked(trajectoire)
                }
                binding.homeRecyclerTrajet1.setHasFixedSize(true)

                binding.homeRecyclerTrajet2.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.homeRecyclerTrajet2.adapter = Trajet2Adapter(trajet2) {
                        trajectoire -> onTrajetClicked(trajectoire)
                }
                binding.homeRecyclerTrajet2.setHasFixedSize(true)
            } else {
                println("Trajets non trouvé")
            }
        }

    }

    private fun onTrajetClicked(trajectoire: Trajet) {
        val intent = Intent(requireContext(), ReserverPlaceActivity::class.java)
        intent.putExtra("trajet", trajectoire) // Envoyer l'objet Trajet
        startActivity(intent)
    }

}