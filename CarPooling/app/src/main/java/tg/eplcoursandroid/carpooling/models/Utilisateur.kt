package tg.eplcoursandroid.carpooling.models

data class Utilisateur(
    val uid: String?,
    val email: String?,
    val nom: String?,
    val trajets: List<Trajet>?,
    val photoUrl: String? = null
)