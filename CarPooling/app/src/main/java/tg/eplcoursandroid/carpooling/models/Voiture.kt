package tg.eplcoursandroid.carpooling.models

import java.io.Serializable

data class Voiture (
     var numero : String = "",
     var places : Int = 0
 ) : Serializable