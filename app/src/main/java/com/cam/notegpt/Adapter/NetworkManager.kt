package com.cam.notegpt.Adapter

import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import com.cam.notegpt.Database.OpenAIAPIClient
import org.json.JSONException

class NetworkManager {
    private val client = OpenAIAPIClient()

    interface Callback {
        fun onSuccess(content: String)
        fun onError(e: Exception)
    }

    fun rephraseNote(noteContent: String, callback: Callback) {
        client.AskForNoteRephrase(noteContent, object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback.onError(e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                try {
                    val responseData = response.body?.string()
                    val json = JSONObject(responseData)
                    val choices = json.getJSONArray("choices")
                    val firstChoice = choices.getJSONObject(0)
                    val message = firstChoice.getJSONObject("message")
                    val content = message.getString("content")
                    callback.onSuccess(content)
                } catch (e: JSONException) {
                    callback.onError(e)
                }
            }
        })
    }

    fun generateTitle(noteContent: String, callback: Callback) {
        client.AskForTitle(noteContent, object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback.onError(e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                try {
                    val responseData = response.body?.string()
                    val json = JSONObject(responseData)
                    val choices = json.getJSONArray("choices")
                    val firstChoice = choices.getJSONObject(0)
                    val message = firstChoice.getJSONObject("message")
                    val content = message.getString("content")
                    callback.onSuccess(content)
                } catch (e: JSONException) {
                    callback.onError(e)
                }
            }
        })
    }
}
