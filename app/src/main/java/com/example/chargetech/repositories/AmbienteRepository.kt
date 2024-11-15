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
    private val BASE_URL = "https://bc060a2d-0ae5-421b-bd41-9bdac6557ef0-00-6zly8cho0nxy.riker.replit.dev"

    fun getById(id_ambiente: Int, callback: (Ambiente?, String?) -> Unit) {
        val request = Request.Builder()
            .url("$BASE_URL/ambiente/$id_ambiente")
            .get()
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
                            val ambiente = Ambiente(
                                id_ambiente = jsonObject.getInt("id_ambiente"),
                                nome = jsonObject.getString("nome"),
                                descricao = jsonObject.getString("descricao"),
                                id_usuario = 0,
                                dispositivos = mutableListOf()
                            )
                            callback(ambiente, null)
                        } catch (e: Exception) {
                            callback(null, "Erro ao processar a resposta do servidor")
                        }
                    } else {
                        callback(null, "Resposta vazia do servidor")
                    }
                } else {
                    callback(null, "Falha ao buscar ambiente")
                }
            }
        })
    }

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

    fun update(id_ambiente: Int, nome: String, descricao: String, callback: (JSONObject?, String?) -> Unit) {
        val jsonBody = """
        {
            "nome": "$nome",
            "descricao": "$descricao"
        }
    """.trimIndent()

        val request = Request.Builder()
            .url("$BASE_URL/ambiente/$id_ambiente")
            .put(jsonBody.toRequestBody("application/json".toMediaType()))
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
                    callback(null, "Falha ao atualizar ambiente")
                }
            }
        })
    }

    fun delete(id_ambiente: Int, callback: (Boolean, String?) -> Unit) {
        val request = Request.Builder()
            .url("$BASE_URL/ambiente/$id_ambiente")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, "Falha ao conectar ao servidor")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, "Falha ao excluir ambiente")
                }
            }
        })
    }
}