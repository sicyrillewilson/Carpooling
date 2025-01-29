package tg.eplcoursandroid.carpooling.models

data class Evaluation(
    val idConducteur: String,
    val idPassager: String,
    val note: Int,
    val commentaire: String
)