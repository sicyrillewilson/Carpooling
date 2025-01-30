package tg.eplcoursandroid.carpooling.models

data class Trajet(
    var idTrajet: String = "",
    var idConducteur: String = "",
    var itineraire: String = "",
    var heureDepart: String = "",
    var lieuDepart: String = "",
    var prixParPassager: Double = 0.0,
    var destination: String = ""
) {
    // Afficher les détails du trajet
    fun afficherDetailsTrajet() {
        println("Itinéraire : $itineraire")
        println("Heure de départ : $heureDepart")
        println("Prix par passager : $prixParPassager")
    }
}