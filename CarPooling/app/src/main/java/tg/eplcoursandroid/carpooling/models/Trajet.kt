package tg.eplcoursandroid.carpooling.models

data class Trajet(
    val idTrajet: String,
    val idConducteur: String,
    val itineraire: String,
    val heureDepart: String,
    val lieuDepart: String,
    val prixParPassager: Double,
    val placesDisponibles: Int,
    val destination: String
) {
    // Afficher les détails du trajet
    fun afficherDetailsTrajet() {
        println("Itinéraire : $itineraire")
        println("Heure de départ : $heureDepart")
        println("Prix par passager : $prixParPassager")
        println("Places disponibles : $placesDisponibles")
    }
}