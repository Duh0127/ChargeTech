package com.example.chargetech.repositories

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class LoginRepository () {
    private val client = OkHttpClient()
    private val BASE_URL = "https://bc060a2d-0ae5-421b-bd41-9bdac6557ef0-00-6zly8cho0nxy.riker.replit.dev"

    fun login(email: String, password: String, callback: (JSONObject?, String?) -> Unit) {
        val jsonBody = """
            {
                "email": "${email}",
                "senha": "${password}"
            }
        """.trimIndent()

        val request = Request.Builder()
            .url("$BASE_URL/login")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, "Falha ao conectar ao servidor")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        try {
                            val jsonObject = JSONObject(responseBody)
                            callback(jsonObject, null)
                        } catch (e: Exception) {
                            callback(null, "Erro ao processar a resposta do servidor")
                        }
                    } else {
                        callback(null, "Resposta vazia do servidor")
                    }
                } else {
                    callback(null, "Credenciais inválidas")
                }
            }
        })
    }
}