package tg.eplcoursandroid.carpooling.models

data class Reservation(
    val idReservation: String,
    val idTrajet: String,
    val idPassager: String,
    val statut: String // "En attente", "Confirmée", "Annulée"
)