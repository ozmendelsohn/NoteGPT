package com.cam.notegpt.Database

import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException

class NetworkRepository {
    private val client = OkHttpClient()

    fun sendPostRequest(title: String, noteContent: String, apiKey: String, callback: Callback) {
        val mediaType = "application/json".toMediaTypeOrNull()
        val body = RequestBody.create(mediaType,
            """{
                "model": "gpt-3.5-turbo",
                "messages": [{"role": "user", "content": "$title $noteContent"}],
                "temperature": 0.7
            }"""
        )

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        client.newCall(request).enqueue(callback)
    }
}
