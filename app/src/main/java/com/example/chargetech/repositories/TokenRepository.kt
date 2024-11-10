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

class TokenRepository {

    private val client = OkHttpClient()
    private val BASE_URL = "https://569586ab-db4d-4447-b516-9032818e8306-00-25w7cq51q41rq.janeway.replit.dev"

    fun decodeToken(token: String, callback: (JSONObject?, String?) -> Unit) {
        // Construção do corpo da requisição com o token
        val jsonBody = """
            {
                "token": "$token"
            }
        """.trimIndent()

        // Criando a requisição
        val request = Request.Builder()
            .url("$BASE_URL/decodeToken")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

        // Enviando a requisição assíncrona
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Caso haja falha ao conectar, retornamos um erro
                callback(null, "Falha ao conectar ao servidor: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            // Tentando converter a resposta para JSON
                            val jsonObject = JSONObject(responseBody)
                            callback(jsonObject, null)  // Retorna o json caso tudo ocorra bem
                        } else {
                            callback(null, "Resposta vazia do servidor")
                        }
                    } else {
                        callback(null, "Token inválido ou erro na requisição")
                    }
                } catch (e: Exception) {
                    callback(null, "Erro ao processar a resposta: ${e.message}")
                }
            }
        })
    }
}
