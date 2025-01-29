package tg.eplcoursandroid.carpooling.service

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import tg.eplcoursandroid.carpooling.models.Passager

class PassagerService {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("passagers")
    fun ajouterPassager(passager: Passager, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val id = database.push().key  // Génère un ID unique
        if (id != null) {
            database.child(id).setValue(passager)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { exception -> onFailure(exception) }
        } else {
            onFailure(Exception("Impossible de générer un ID unique"))
        }
    }

    fun supprimerPassager(uid: String) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("passagers/$uid")
        ref.removeValue()
            .addOnSuccessListener {
                println("Passager supprimé avec succès !")
            }
            .addOnFailureListener { e ->
                println("Erreur : ${e.message}")
            }
    }

    fun modifierPassager(passager: Passager) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("passagers/${passager.utilisateur.uid}")
        ref.setValue(passager)
            .addOnSuccessListener {
                println("Passager modifié avec succès !")
            }
            .addOnFailureListener { e ->
                println("Erreur : ${e.message}")
            }
    }

    fun trouverPassager(uid: String, callback: (Passager?) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("passagers/$uid")
        ref.get().addOnSuccessListener { snapshot ->
            val passager = snapshot.getValue(Passager::class.java)
            callback(passager)
        }.addOnFailureListener { e ->
            println("Erreur : ${e.message}")
            callback(null)
        }
    }

    fun listerPassagers(callback: (List<Passager>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("passagers")
        ref.get().addOnSuccessListener { snapshot ->
            val passagers = snapshot.children.mapNotNull { it.getValue(Passager::class.java) }
            callback(passagers)
        }.addOnFailureListener { e ->
            println("Erreur : ${e.message}")
            callback(emptyList())
        }
    }

}