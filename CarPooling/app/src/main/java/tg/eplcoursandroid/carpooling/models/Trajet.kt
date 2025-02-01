package tg.eplcoursandroid.carpooling.models

import java.io.Serializable

data class Trajet(
    var idTrajet: String = "",
    var idConducteur: String = "",
    var itineraire: String = "",
    var places: String = "",
    var heureDepart: String = "",
    var lieuDepart: String = "",
    var prixParPassager: Double = 0.0,
    var listIdPassagerReservation: MutableList<String> = mutableListOf(),
    var listIdPassager: MutableList<String> = mutableListOf(),
    var destination: String = ""
) : Serializable 