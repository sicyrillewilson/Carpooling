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
import tg.eplcoursandroid.carpooling.models.Trajet
import tg.eplcoursandroid.carpooling.service.ConducteurService

class Trajet2Adapter (private val trajets: List<Trajet>, private val onTrajetClicked: (Trajet) -> Unit) : RecyclerView.Adapter<Trajet2Adapter.TicketViewHolder>() {

    // ViewHolder inner class
    class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val destinationTextView: TextView = itemView.findViewById(R.id.itm_trajet2_destination)
        val lieuDepartTextView: TextView = itemView.findViewById(R.id.itm_trajet2_lieu_de_depart)
        val heureDepartTextView: TextView = itemView.findViewById(R.id.itm_trajet2_heure_de_depart)
        val prixTextView: TextView = itemView.findViewById(R.id.itm_trajet2_prix)
        val profileImageView: CircleImageView = itemView.findViewById(R.id.itm_trajet2_conducteur_profil)
        // LinearLayout pour les étoiles
        val starContainer: LinearLayout = itemView.findViewById(R.id.itm_trajet2_starContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_trajet2, parent, false)
        return TicketViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val currentTrajet = trajets[position]
        val conducteurService = ConducteurService()

        conducteurService.trouverConducteur(currentTrajet.idConducteur) { conducteur ->
            if (conducteur != null) {
                // Si le conducteur est trouvé
                println("Conducteur trouvé : ${conducteur.utilisateur?.nom}")
                holder.destinationTextView.text = currentTrajet.destination
                holder.lieuDepartTextView.text = currentTrajet.lieuDepart
                holder.heureDepartTextView.text = currentTrajet.heureDepart
                holder.prixTextView.text = currentTrajet.prixParPassager.toString()

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
        // Ajout du listener pour détecter les clics sur l'élément du RecyclerView
        holder.itemView.setOnClickListener {
            onTrajetClicked(currentTrajet)
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