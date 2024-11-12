package com.example.chargetech.repositories

import com.example.chargetech.models.ConsumoEnergetico
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ConsumoEnergeticoRepository {

    private val client = OkHttpClient()
    private val BASE_URL = "https://569586ab-db4d-4447-b516-9032818e8306-00-25w7cq51q41rq.janeway.replit.dev"

    fun register(consumoEnergetico: ConsumoEnergetico, callback: (JSONObject?, String?) -> Unit) {
        val jsonBody = """
            {
                "id_dispositivo": "${consumoEnergetico.id_dispositivo}",
                "consumo": "${consumoEnergetico.consumo}"
            }
        """.trimIndent()

        val request = Request.Builder()
            .url("$BASE_URL/consumo_energetico")
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

    fun deleteById(id_consumo_energetico: Int, callback: (Boolean) -> Unit) {
        val request = Request.Builder()
            .url("$BASE_URL/consumo_energetico/$id_consumo_energetico")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false) // Falha ao conectar ao servidor
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(true) // Exclusão bem-sucedida
                } else {
                    callback(false) // Erro no servidor ou ID inválido
                }
            }
        })
    }

}