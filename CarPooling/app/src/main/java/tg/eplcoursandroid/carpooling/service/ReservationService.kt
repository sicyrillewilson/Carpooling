package tg.eplcoursandroid.carpooling.service

import com.google.firebase.database.FirebaseDatabase
import tg.eplcoursandroid.carpooling.models.Reservation

class ReservationService {

    fun ajouterReservation(reservation: Reservation) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("reservations/${reservation.idReservation}")
        ref.setValue(reservation)
            .addOnSuccessListener {
                println("Réservation ajoutée avec succès !")
            }
            .addOnFailureListener { e ->
                println("Erreur : ${e.message}")
            }
    }

    fun supprimerReservation(idReservation: String) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("reservations/$idReservation")
        ref.removeValue()
            .addOnSuccessListener {
                println("Réservation supprimée avec succès !")
            }
            .addOnFailureListener { e ->
                println("Erreur : ${e.message}")
            }
    }

    fun modifierReservation(reservation: Reservation) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("reservations/${reservation.idReservation}")
        ref.setValue(reservation)
            .addOnSuccessListener {
                println("Réservation modifiée avec succès !")
            }
            .addOnFailureListener { e ->
                println("Erreur : ${e.message}")
            }
    }

    fun trouverReservation(idReservation: String, callback: (Reservation?) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("reservations/$idReservation")
        ref.get().addOnSuccessListener { snapshot ->
            val reservation = snapshot.getValue(Reservation::class.java)
            callback(reservation)
        }.addOnFailureListener { e ->
            println("Erreur : ${e.message}")
            callback(null)
        }
    }

    fun listerReservations(callback: (List<Reservation>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("reservations")
        ref.get().addOnSuccessListener { snapshot ->
            val reservations = snapshot.children.mapNotNull { it.getValue(Reservation::class.java) }
            callback(reservations)
        }.addOnFailureListener { e ->
            println("Erreur : ${e.message}")
            callback(emptyList())
        }
    }
}