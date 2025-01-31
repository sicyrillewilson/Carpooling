package tg.eplcoursandroid.carpooling.models

import java.io.Serializable

data class Reservation(
    val idReservation: String,
    val idTrajet: String,
    val idPassager: String,
    val statut: String // "En attente", "Confirmée", "Annulée"
): Serializable