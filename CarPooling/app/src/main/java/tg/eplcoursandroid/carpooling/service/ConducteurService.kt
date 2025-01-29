package tg.eplcoursandroid.carpooling.service

import com.google.firebase.database.FirebaseDatabase
import tg.eplcoursandroid.carpooling.models.Conducteur

class ConducteurService {

    fun ajouterConducteur(conducteur: Conducteur) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("conducteurs/${conducteur.utilisateur?.uid}")
        ref.setValue(conducteur)
            .addOnSuccessListener {
                println("Conducteur ajouté avec succès !")
            }
            .addOnFailureListener { e ->
                println("Erreur : ${e.message}")
            }
    }

    fun supprimerConducteur(uid: String) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("conducteurs/$uid")
        ref.removeValue()
            .addOnSuccessListener {
                println("Conducteur supprimé avec succès !")
            }
            .addOnFailureListener { e ->
                println("Erreur : ${e.message}")
            }
    }

    fun modifierConducteur(conducteur: Conducteur) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("conducteurs/${conducteur.utilisateur?.uid}")
        ref.setValue(conducteur)
            .addOnSuccessListener {
                println("Conducteur modifié avec succès !")
            }
            .addOnFailureListener { e ->
                println("Erreur : ${e.message}")
            }
    }

    fun trouverConducteur(uid: String, callback: (Conducteur?) -> Unit){
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("conducteurs/$uid")
        ref.get().addOnSuccessListener { snapshot ->
            val conducteur = snapshot.getValue(Conducteur::class.java)
            callback(conducteur)
        }.addOnFailureListener { e ->
            println("Erreur : ${e.message}")
            callback(null)
        }
    }

    fun listerConducteurs(callback: (List<Conducteur>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("conducteurs")
        ref.get().addOnSuccessListener { snapshot ->
            val conducteurs = snapshot.children.mapNotNull { it.getValue(Conducteur::class.java) }
            callback(conducteurs)
        }.addOnFailureListener { e ->
            println("Erreur : ${e.message}")
            callback(emptyList())
        }
    }
}