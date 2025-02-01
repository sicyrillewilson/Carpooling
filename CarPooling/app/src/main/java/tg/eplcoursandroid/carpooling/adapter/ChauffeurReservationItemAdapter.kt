package tg.eplcoursandroid.carpooling.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import tg.eplcoursandroid.carpooling.R
import tg.eplcoursandroid.carpooling.models.Chat
import tg.eplcoursandroid.carpooling.models.Conducteur
import tg.eplcoursandroid.carpooling.models.Trajet
import tg.eplcoursandroid.carpooling.service.ConducteurService

class ChauffeurReservationItemAdapter (private val trajets: List<Trajet>, private val onTrajetClick: (Trajet) -> Unit) : RecyclerView.Adapter<ChauffeurReservationItemAdapter.TicketViewHolder>() {

    // ViewHolder inner class
    class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val destinationTextView: TextView = itemView.findViewById(R.id.itm_chauffeur_reservation_destination)
        val lieuDepartTextView: TextView = itemView.findViewById(R.id.itm_chauffeur_reservation_lieu_de_depart)
        val heureDepartTextView: TextView = itemView.findViewById(R.id.itm_chauffeur_reservation_heure_de_depart)
        val prixTextView: TextView = itemView.findViewById(R.id.itm_chauffeur_reservation_prix)
        val profileImageView: CircleImageView = itemView.findViewById(R.id.itm_chauffeur_reservation_conducteur_profil)
        // LinearLayout pour les étoiles
        val starContainer: LinearLayout = itemView.findViewById(R.id.itm_chauffeur_reservation_starContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_chauffeur_reservation, parent, false)
        return TicketViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val currentTicket = trajets.get(position)
        val conducteurService: ConducteurService = ConducteurService()

        conducteurService.trouverConducteur(currentTicket.idConducteur) { conducteur ->
            if (conducteur != null) {
                // Si le conducteur est trouvé
                println("Conducteur trouvé : ${conducteur.utilisateur?.nom}")
                holder.destinationTextView.text = currentTicket.destination
                holder.lieuDepartTextView.text = currentTicket.lieuDepart
                holder.heureDepartTextView.text = currentTicket.heureDepart
                holder.prixTextView.text = currentTicket.prixParPassager.toString()

                // Charger l'image du conducteur
                Glide.with(holder.itemView.context)
                    .load(conducteur.utilisateur?.photoUrl) // Image du conducteur
                    .placeholder(R.drawable.default_profile) // Image temporaire en attendant le chargement
                    .error(R.drawable.default_profile) // Image affichée si l'URL est invalide ou absente
                    .into(holder.profileImageView)

                // Afficher les étoiles en fonction de la note
                displayStars(conducteur.note ?: 0, holder.starContainer, holder.itemView.context)
            } else {
                // Si le conducteur n'est pas trouvé
                println("Conducteur non trouvé")
            }
        }
        holder.itemView.setOnClickListener {
            onTrajetClick(currentTicket)
        }
    }

    override fun getItemCount(): Int = trajets.size

    private fun displayStars(note: Int, starContainer: LinearLayout, context: Context) {
        // Vider le LinearLayout avant d'ajouter les étoiles
        starContainer.removeAllViews()

        // Ajouter les étoiles selon la note
        for (i in 1..5) {
            val starImage = ImageView(context)
            if (i <= note) {
                starImage.setImageResource(R.drawable.star_plein) // Étoile remplie
            } else {
                starImage.setImageResource(R.drawable.star) // Étoile vide
            }
            starImage.layoutParams = LinearLayout.LayoutParams(20.dpToPx(context), 20.dpToPx(context)) // Ajuster la taille des étoiles
            starContainer.addView(starImage)
        }
    }

    // Extension pour convertir dp en pixels
    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}