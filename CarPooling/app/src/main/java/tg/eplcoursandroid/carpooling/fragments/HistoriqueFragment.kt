package tg.eplcoursandroid.carpooling.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import tg.eplcoursandroid.carpooling.adapter.HistoriqueAdapter
import tg.eplcoursandroid.carpooling.databinding.FragmentHistoriqueBinding
import tg.eplcoursandroid.carpooling.models.Trajet
import tg.eplcoursandroid.carpooling.service.AuthService
import tg.eplcoursandroid.carpooling.service.TrajetService
import tg.eplcoursandroid.carpooling.service.UtilisateurService

class HistoriqueFragment : Fragment() {

    private var _binding: FragmentHistoriqueBinding? = null
    private val binding get() = _binding!!

    private val authService = AuthService()
    private val utilisateurService = UtilisateurService()
    private val currentUser = authService.getCurrentUser()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_historique, container, false)
        _binding = FragmentHistoriqueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var trajetService = TrajetService()
        var trajets: List<Trajet> = listOf()
        var trajetsHistorique: MutableList<Trajet> = mutableListOf()

        trajetService.listerTrajets { traj ->
            if (traj != null) {
                trajets = traj

                for (trajet in trajets) {
                    for (idPassager in trajet.listIdPassager) {
                        if (idPassager == "1"){
                            trajetsHistorique.add(trajet)
                        }
                    }
                }

                binding.fragmentHistoriqueRecyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.fragmentHistoriqueRecyclerview.adapter = HistoriqueAdapter(trajetsHistorique)
                binding.fragmentHistoriqueRecyclerview.setHasFixedSize(true)

            } else {
                println("Trajets non trouvé")
            }
        }

    }

    private fun chargerHistorique() {
        val trajetService = TrajetService()
        val trajetsHistorique: MutableList<Trajet> = mutableListOf()

        trajetService.listerTrajets { trajets ->
            if (trajets != null) {
                requireActivity().runOnUiThread {
                    trajetsHistorique.clear()

                    for (trajet in trajets) {
                        if ("1" in trajet.listIdPassager) {
                            trajetsHistorique.add(trajet)
                        }
                    }
                    binding.fragmentHistoriqueRecyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    binding.fragmentHistoriqueRecyclerview.adapter = HistoriqueAdapter(trajetsHistorique)
                    binding.fragmentHistoriqueRecyclerview.setHasFixedSize(true)
                    binding.fragmentHistoriqueRecyclerview.adapter?.notifyDataSetChanged()
                }
            } else {
                println("Trajets non trouvés")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        chargerHistorique()
    }
}