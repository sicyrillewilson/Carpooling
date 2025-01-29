package tg.eplcoursandroid.carpooling.service

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import tg.eplcoursandroid.carpooling.models.Utilisateur

class UtilisateurService {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("utilisateurs")

    // Ajouter un utilisateur
    fun ajouterUtilisateur(utilisateur: Utilisateur, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val id = database.push().key  // Génère un ID unique
        if (id != null) {
            database.child(id).setValue(utilisateur)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { exception -> onFailure(exception) }
        } else {
            onFailure(Exception("Impossible de générer un ID unique"))
        }
    }

    fun supprimerUtilisateur(uid: String) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("utilisateurs/$uid")

        ref.removeValue()
            .addOnSuccessListener {
                println("Utilisateur supprimé avec succès !")
            }
            .addOnFailureListener { e ->
                println("Erreur : ${e.message}")
            }
    }

    fun modifierUtilisateur(utilisateur: Utilisateur) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("utilisateurs/${utilisateur.uid}")

        // Convertir l'objet Utilisateur en Map pour Firebase
        val utilisateurMap = hashMapOf(
            "uid" to utilisateur.uid,
            "email" to utilisateur.email,
            "nom" to utilisateur.nom
        )

        ref.setValue(utilisateurMap)
            .addOnSuccessListener {
                println("Utilisateur modifié avec succès !")
            }
            .addOnFailureListener { e ->
                println("Erreur : ${e.message}")
            }
    }

    fun trouverUtilisateur(uid: String, callback: (Utilisateur?) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("utilisateurs/$uid")

        ref.get().addOnSuccessListener { snapshot ->
            val utilisateur = snapshot.getValue(Utilisateur::class.java)
            callback(utilisateur)
        }.addOnFailureListener { e ->
            println("Erreur : ${e.message}")
            callback(null)
        }
    }

    fun listerUtilisateurs(callback: (List<Utilisateur>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("utilisateurs")

        ref.get().addOnSuccessListener { snapshot ->
            val utilisateurs = snapshot.children.mapNotNull { it.getValue(Utilisateur::class.java) }
            callback(utilisateurs)
        }.addOnFailureListener { e ->
            println("Erreur : ${e.message}")
            callback(emptyList())
        }
    }

}