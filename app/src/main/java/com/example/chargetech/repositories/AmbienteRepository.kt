package com.example.chargetech.repositories

import android.widget.Toast
import com.example.chargetech.models.Ambiente
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class AmbienteRepository {

    private val client = OkHttpClient()
    private val BASE_URL = "https://569586ab-db4d-4447-b516-9032818e8306-00-25w7cq51q41rq.janeway.replit.dev"

    fun register(nome: String, descricao: String, id_usuario: Int, callback: (JSONObject?, String?) -> Unit) {
        val jsonBody = """
            {
                "nome": "$nome",
                "descricao": "$descricao",
                "id_usuario": $id_usuario
            }
        """.trimIndent()

        val request = Request.Builder()
            .url("$BASE_URL/ambiente")
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
                    callback(null, "Informacoes Invalidas")
                }
            }
        })
    }




}