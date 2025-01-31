package tg.eplcoursandroid.carpooling.models

import java.io.Serializable


class Passager(
    val utilisateur: Utilisateur,
    val nom : String

) : Serializable  {
    // Rechercher un trajet
    fun rechercherTrajet(destination: String, localisationActuelle: String) {
        // Logique pour rechercher un trajet dans Firebase Realtime Database
    }

    // Réserver un trajet
    fun reserverTrajet(idTrajet: String) {
        // Logique pour réserver un trajet
    }

    // Évaluer un conducteur
    fun evaluerConducteur(idConducteur: String, note: Int, commentaire: String) {
        // Logique pour enregistrer une évaluation dans Firebase
    }
}