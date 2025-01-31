package tg.eplcoursandroid.carpooling.models

import java.io.Serializable

data class Evaluation(
    val idConducteur: String,
    val idPassager: String,
    val note: Int,
    val commentaire: String
) : Serializable