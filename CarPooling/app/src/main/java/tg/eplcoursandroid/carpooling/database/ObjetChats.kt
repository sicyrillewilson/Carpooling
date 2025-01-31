package tg.eplcoursandroid.carpooling.database

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tg.eplcoursandroid.carpooling.models.Chat

object ObjetChats {
    fun addChat(context: Context, chat: Chat){
        val chats : MutableList<Chat> = loadChats(context).toMutableList()
        chats.add(chat)
        saveChats(context, chats)
    }

    fun saveChats(context: Context, chats: List<Chat>) {
        val sharedPreferences = context.getSharedPreferences("ChatsPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(chats)
        editor.putString("chats", json)
        editor.apply()
    }


    fun loadChats(context: Context): List<Chat> {
        val sharedPreferences = context.getSharedPreferences("ChatsPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("chats", null)
        val type = object : TypeToken<List<Chat>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }
}