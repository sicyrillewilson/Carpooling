package tg.eplcoursandroid.carpooling.service

import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import tg.eplcoursandroid.carpooling.models.Utilisateur

class UtilisateurService {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("utilisateurs")

    // Ajouter un utilisateur
    fun ajouterUtilisateur(utilisateur: Utilisateur, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        //val id = database.push().key  // Génère un ID unique
        val id = utilisateur.uid  // Génère un ID unique
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
        Log.d("TrouverUser", "En cours1")
        val ref = database.getReference("utilisateurs/$uid")

        Log.d("TrouverUser", "En cours2")
        ref.get().addOnSuccessListener { snapshot ->
            Log.d("TrouverUser", "En cours3")
            val utilisateur = Utilisateur(snapshot.child("uid").value.toString(), snapshot.child("email").value.toString(), snapshot.child("nom").value.toString(),snapshot.child("photoUrl").value.toString())
            Log.d("TrouverUser", "En cours4")
            callback(utilisateur)
            Log.d("TrouverUser", "En cours5")
        }.addOnFailureListener { e ->
            Log.d("TrouverUser", "En cours3-1")
            println("Erreur : ${e.message}")
            Log.d("TrouverUser", "En cours4-1")
            callback(null)
        }
        Log.d("TrouverUser", "En cours fin")
    }

    /*fun trouverUtilisateur(uid: String, callback: (Utilisateur?) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("utilisateurs/$uid")

        Log.d("TrouverUser", "En cours1 - Recherche de l'utilisateur avec UID: $uid")

        ref.get().addOnSuccessListener { snapshot ->
            Log.d("TrouverUser", "En cours3 - Données reçues : ${snapshot.value}")
            Log.d("TrouverUser", "En cours3 - Données reçues : ${snapshot.child("uid").value}")

            if (snapshot.exists()) {
                Log.d("TrouverUser", "En cours4-0 - Utilisateur trouvé en cours")
                //val utilisateur = snapshot.getValue(Utilisateur::class.java)
                val utilisateur = Utilisateur(snapshot.child("uid").value.toString(), snapshot.child("email").value.toString(), snapshot.child("nom").value.toString(),snapshot.child("photoUrl").value.toString())
                Log.d("TrouverUser", "En cours4 - Utilisateur trouvé: ${utilisateur?.nom}")
                callback(utilisateur)
            } else {
                Log.e("TrouverUser", "Utilisateur non trouvé pour UID: $uid")
                callback(null)
            }
        }.addOnFailureListener { e ->
            Log.e("TrouverUser", "Firebase Error: ${e.message}")
            callback(null)
        }

        Log.d("TrouverUser", "En cours fin")
    }*/

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