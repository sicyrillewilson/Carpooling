package tg.eplcoursandroid.carpooling.models

import java.io.Serializable

data class Message(
    var id: String = "",
    var senderId: String = "",
    var receiverId: String = "",
    var content: String = "",
    var timestamp: Long = 0
) : Serializable