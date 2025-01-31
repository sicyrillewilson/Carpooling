package tg.eplcoursandroid.carpooling.database

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tg.eplcoursandroid.carpooling.models.Conducteur
import tg.eplcoursandroid.carpooling.models.Utilisateur

object ObjetUtilisateur {
    fun saveUtilisateur(context: Context, utilisateur: Utilisateur) {
        val sharedPreferences = context.getSharedPreferences("UtilisateurPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(utilisateur)
        editor.putString("utilisateur", json)
        editor.apply()
    }


    fun loadUtilisateur(context: Context): Utilisateur {
        val sharedPreferences = context.getSharedPreferences("UtilisateurPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("utilisateur", null)
        val type = object : TypeToken<Utilisateur>() {}.type
        return gson.fromJson(json, type) ?: Utilisateur()
    }
}