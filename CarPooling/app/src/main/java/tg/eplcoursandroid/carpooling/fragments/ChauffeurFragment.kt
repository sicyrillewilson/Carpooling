package tg.eplcoursandroid.carpooling.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import tg.eplcoursandroid.carpooling.FormulaireChauffeurActivity
import tg.eplcoursandroid.carpooling.FormulaireTrajetActivity
import tg.eplcoursandroid.carpooling.R
import tg.eplcoursandroid.carpooling.SignUpActivity
import tg.eplcoursandroid.carpooling.adapter.ChauffeurItemAdapter
import tg.eplcoursandroid.carpooling.adapter.Trajet1Adapter
import tg.eplcoursandroid.carpooling.adapter.Trajet3Adapter
import tg.eplcoursandroid.carpooling.database.ObjetConducteur
import tg.eplcoursandroid.carpooling.databinding.FragmentChauffeurBinding
import tg.eplcoursandroid.carpooling.databinding.FragmentHomeBinding
import tg.eplcoursandroid.carpooling.databinding.FragmentPreChauffeurBinding
import tg.eplcoursandroid.carpooling.models.Conducteur
import tg.eplcoursandroid.carpooling.models.Trajet
import tg.eplcoursandroid.carpooling.service.AuthService
import tg.eplcoursandroid.carpooling.service.ConducteurService
import tg.eplcoursandroid.carpooling.service.TrajetService
import tg.eplcoursandroid.carpooling.service.UtilisateurService

class ChauffeurFragment : Fragment() {

    private var _binding: FragmentPreChauffeurBinding? = null
    private val binding get() = _binding!!
    private var _binding2: FragmentChauffeurBinding? = null
    private val binding2 get() = _binding2!!

    private val authService = AuthService()
    private val conducteurService = ConducteurService()
    private val currentUser = authService.getCurrentUser()
    private var currentConducteur = Conducteur()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        // Inflate the layout for this fragment
        if (currentConducteur.utilisateur == null) {
            Log.e("ChauffeurFragment", "Conducteur non trouvé non trouvé 3")
            _binding = FragmentPreChauffeurBinding.inflate(inflater, container, false)
            return binding.root
            //return inflater.inflate(R.layout.fragment_pre_chauffeur, container, false)
        }else{
            Log.e("ChauffeurFragment", "Conducteur trouvé")
            _binding2 = FragmentChauffeurBinding.inflate(inflater, container, false)
            return binding2.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (currentConducteur.utilisateur == null) {
            nonConducteur()
        }else{
            conducteur()
        }

    }

    private fun nonConducteur() {
        binding.fragmentPreChauffeurDevenirChauffeur.setOnClickListener {
            val intent = Intent(requireContext(), FormulaireChauffeurActivity::class.java)
            startActivity(intent)
        }
    }

    private fun conducteur() {
        binding2.fragmentChauffeurAddIcon.setOnClickListener {
            val intent = Intent(requireContext(), FormulaireTrajetActivity::class.java)
            startActivity(intent)
        }
        var trajetService = TrajetService()
        var trajets: List<Trajet> = listOf()
        var trajetsConducteur: MutableList<Trajet> = mutableListOf()

        trajetService.listerTrajets { traj ->
            if (traj != null) {
                trajets = traj

                for (trajet in trajets) {
                    if (trajet.idConducteur == currentConducteur.utilisateur?.uid) {
                        trajetsConducteur.add(trajet)
                    }
                }

                binding2.fragmentChauffeurRecyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding2.fragmentChauffeurRecyclerview.adapter = ChauffeurItemAdapter(trajetsConducteur)
                binding2.fragmentChauffeurRecyclerview.setHasFixedSize(true)

            } else {
                println("Trajets non trouvé")
            }
        }
    }

}