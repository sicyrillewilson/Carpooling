package tg.eplcoursandroid.carpooling.models


class Conducteur(
    val utilisateur: Utilisateur? = null,
    val numero: String? = null,
    val note: Int? = null,
    val voiture: String? = null //Matricule
) {

    // Constructeur par recopie
    constructor(conducteur: Conducteur) : this(
        utilisateur = conducteur.utilisateur,
        numero = conducteur.numero,
        note = conducteur.note,
        voiture = conducteur.voiture
    )

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
