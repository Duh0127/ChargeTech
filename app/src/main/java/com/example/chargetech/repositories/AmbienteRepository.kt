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

    fun getById(id_ambiente: Int, callback: (Ambiente?, String?) -> Unit) {
        val request = Request.Builder()
            .url("$BASE_URL/ambiente/$id_ambiente")  // Supondo que a URL da API aceite um ID para buscar um ambiente específico
            .get()  // Usamos GET para obter dados
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
                            // Processar a resposta JSON para criar um objeto Ambiente
                            val jsonObject = JSONObject(responseBody)
                            val ambiente = Ambiente(
                                id_ambiente = jsonObject.getInt("id_ambiente"),
                                nome = jsonObject.getString("nome"),
                                descricao = jsonObject.getString("descricao"),
                                id_usuario = 0,
                                dispositivos = mutableListOf()
                            )
                            callback(ambiente, null)  // Retorna o objeto Ambiente
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
            .url("$BASE_URL/ambiente/$id_ambiente")  // Assume que a URL da API aceita um ID de ambiente para atualização
            .put(jsonBody.toRequestBody("application/json".toMediaType()))  // Usamos PUT para atualização
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
                            callback(jsonObject, null)  // Retorna o JSON atualizado
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
            .url("$BASE_URL/ambiente/$id_ambiente")  // Supondo que a API usa um endpoint com o ID para exclusão
            .delete()  // Usamos DELETE para excluir o recurso
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, "Falha ao conectar ao servidor")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(true, null)  // Sucesso na exclusão
                } else {
                    callback(false, "Falha ao excluir ambiente")
                }
            }
        })
    }



}