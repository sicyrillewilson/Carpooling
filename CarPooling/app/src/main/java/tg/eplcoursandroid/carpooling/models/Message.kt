package tg.eplcoursandroid.carpooling.models

data class Message(
    val id: String = "",
    val text: String,          // Contenu du message
    val senderId: String,      // ID de l'utilisateur qui a envoyé le message
    val timestamp: Long = System.currentTimeMillis() // Date et heure du message
){
    constructor() : this("", "", "", 0L)
}