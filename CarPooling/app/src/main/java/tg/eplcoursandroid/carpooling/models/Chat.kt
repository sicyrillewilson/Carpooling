package tg.eplcoursandroid.carpooling.models

data class Chat(
    var id: String = "",
    var conducteurId: String = "",
    var passagerId: String = "",
    var dernierMessage: String = "",
    var timestamp: Long = System.currentTimeMillis()
)
