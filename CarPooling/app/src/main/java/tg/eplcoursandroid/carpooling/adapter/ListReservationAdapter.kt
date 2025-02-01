package tg.eplcoursandroid.carpooling.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import tg.eplcoursandroid.carpooling.R
import tg.eplcoursandroid.carpooling.models.Trajet
import tg.eplcoursandroid.carpooling.models.Utilisateur
import tg.eplcoursandroid.carpooling.service.ChatService
import tg.eplcoursandroid.carpooling.service.ConducteurService
import tg.eplcoursandroid.carpooling.service.TrajetService
import tg.eplcoursandroid.carpooling.service.UtilisateurService
import java.text.DateFormat
import java.util.Date

class ListReservationAdapter(private val utilisateurs: MutableList<Utilisateur>, private var currentTrajet: Trajet) :
    RecyclerView.Adapter<ListReservationAdapter.ChatViewHolder>() {

    private val trajetService = TrajetService()

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val participantName: TextView = itemView.findViewById(R.id.item_liste_reservation_prenom)
        val email: TextView = itemView.findViewById(R.id.item_liste_reservation_email)
        val accepter: TextView = itemView.findViewById(R.id.item_liste_reservation_accepter)
        val rejeter: TextView = itemView.findViewById(R.id.item_liste_reservation_rejeter)
        val profileImageView: CircleImageView = itemView.findViewById(R.id.item_liste_reservation_profil)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_liste_reservation, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val utilisateur = utilisateurs[position]
        holder.participantName.text = utilisateur.nom
        holder.email.text = utilisateur.email

        holder.accepter.setOnClickListener {
            if (!currentTrajet.listIdPassager.contains(utilisateur.uid.toString())){
                currentTrajet.listIdPassager.add(utilisateur.uid.toString())
            }
            currentTrajet.listIdPassagerReservation.remove(utilisateur.uid.toString())
            trajetService.modifierTrajet(currentTrajet)
            utilisateurs.remove(utilisateur)

            utilisateurs.remove(utilisateur)
            notifyDataSetChanged()
        }

        holder.rejeter.setOnClickListener {
            currentTrajet.listIdPassagerReservation.remove(utilisateur.uid.toString())
            trajetService.modifierTrajet(currentTrajet)
            utilisateurs.remove(utilisateur)

            utilisateurs.remove(utilisateur)
            notifyDataSetChanged()
        }

        // Charger l'image du conducteur
        Glide.with(holder.itemView.context)
            .load(utilisateur.photoUrl) // Image du conducteur
            .placeholder(R.drawable.default_profile) // Image temporaire en attendant le chargement
            .error(R.drawable.default_profile) // Image affichée si l'URL est invalide ou absente
            .into(holder.profileImageView)
    }

    override fun getItemCount() = utilisateurs.size
}
