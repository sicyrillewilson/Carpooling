package tg.eplcoursandroid.carpooling.database

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tg.eplcoursandroid.carpooling.models.Conducteur

object ObjetConducteur {
    fun saveConducteur(context: Context, conducteur: Conducteur) {
        val sharedPreferences = context.getSharedPreferences("ConducteurPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(conducteur)
        editor.putString("tickets", json)
        editor.apply()
    }


    fun loadConducteur(context: Context): Conducteur {
        val sharedPreferences = context.getSharedPreferences("ConducteurPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("tickets", null)
        val type = object : TypeToken<Conducteur>() {}.type
        return gson.fromJson(json, type) ?: Conducteur()
    }
}