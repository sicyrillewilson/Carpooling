package tg.eplcoursandroid.carpooling.models

import java.io.Serializable

data class Utilisateur(
    val uid: String? = null,
    val email: String? = null,
    val nom: String? = null,
    //val trajets: List<Trajet>?,
    val photoUrl: String? = null
) : Serializable