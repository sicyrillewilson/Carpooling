package tg.eplcoursandroid.carpooling.service

import com.google.firebase.database.FirebaseDatabase
import tg.eplcoursandroid.carpooling.models.Evaluation

class EvaluationService {

    fun ajouterEvaluation(evaluation: Evaluation) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("evaluations/${evaluation.idConducteur}/${evaluation.idPassager}")
        ref.setValue(evaluation)
            .addOnSuccessListener {
                println("Évaluation ajoutée avec succès !")
            }
            .addOnFailureListener { e ->
                println("Erreur : ${e.message}")
            }
    }

    fun supprimerEvaluation(idConducteur: String, idPassager: String) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("evaluations/$idConducteur/$idPassager")
        ref.removeValue()
            .addOnSuccessListener {
                println("Évaluation supprimée avec succès !")
            }
            .addOnFailureListener { e ->
                println("Erreur : ${e.message}")
            }
    }

    fun modifierEvaluation(evaluation: Evaluation) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("evaluations/${evaluation.idConducteur}/${evaluation.idPassager}")
        ref.setValue(evaluation)
            .addOnSuccessListener {
                println("Évaluation modifiée avec succès !")
            }
            .addOnFailureListener { e ->
                println("Erreur : ${e.message}")
            }
    }

    fun trouverEvaluation(idConducteur: String, idPassager: String, callback: (Evaluation?) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("evaluations/$idConducteur/$idPassager")
        ref.get().addOnSuccessListener { snapshot ->
            val evaluation = snapshot.getValue(Evaluation::class.java)
            callback(evaluation)
        }.addOnFailureListener { e ->
            println("Erreur : ${e.message}")
            callback(null)
        }
    }

    fun listerEvaluations(callback: (List<Evaluation>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("evaluations")
        ref.get().addOnSuccessListener { snapshot ->
            val evaluations = snapshot.children.flatMap { it.children.mapNotNull { it.getValue(Evaluation::class.java) } }
            callback(evaluations)
        }.addOnFailureListener { e ->
            println("Erreur : ${e.message}")
            callback(emptyList())
        }
    }

}