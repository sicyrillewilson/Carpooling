package tg.eplcoursandroid.carpooling.models


class Conducteur(
    val utilisateur: Utilisateur,
    val nom : String,
    val voiture: String
) {
    // Créer un trajet
    fun creerTrajet(itineraire: String, heureDepart: String, prixParPassager: Double) {
        // Logique pour créer un trajet dans Firebase Realtime Database
    }

    // Gérer une réservation
    fun gererReservation(idReservation: String, statut: String) {
        // Logique pour mettre à jour le statut d'une réservation
    }

    // Voir l'historique des trajets
    fun voirHistoriqueTrajets() {
        // Logique pour récupérer l'historique des trajets depuis Firebase
    }
}