package com.example.chargetech.repositories

import android.util.Log
import com.example.chargetech.models.Dispositivo
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class DeviceRepository {

    private val client = OkHttpClient()
    private val BASE_URL = "https://569586ab-db4d-4447-b516-9032818e8306-00-25w7cq51q41rq.janeway.replit.dev"

    fun getSelectOptions(callback: (List<String>?, String?) -> Unit) {
        val request = Request.Builder()
            .url("$BASE_URL/ambiente/options")
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
                            val jsonArray = JSONArray(responseBody)
                            val ambientes = mutableListOf<String>()

                            if (jsonArray.length() == 0) {
                                callback(ambientes, null)
                            } else {
                                ambientes.add("Selecione o ambiente")
                                for (i in 0 until jsonArray.length()) {
                                    ambientes.add(jsonArray.getString(i))
                                }
                                callback(ambientes, null)
                            }
                        } catch (e: Exception) {
                            callback(null, "Erro ao processar a resposta do servidor")
                        }
                    }
                } else {
                    callback(null, "Ocorreu um erro ao buscar as options de ambiente")
                }
            }
        })
    }

    fun register(dispositivo: Dispositivo, callback: (JSONObject?, String?) -> Unit) {
        val url = "$BASE_URL/dispositivo"

        val json = JSONObject().apply {
            put("id_ambiente", dispositivo.id_ambiente)
            put("nome", dispositivo.nome)
            put("consumo_medio", dispositivo.consumo_medio)
            put("status", dispositivo.status)
        }

        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(body)
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
                    callback(null, "Erro ao criar o dispositivo: ${response.message}")
                }
            }
        })
    }

    fun getById(idDispositivo: Int, callback: (Dispositivo?, String?) -> Unit) {
        val url = "$BASE_URL/dispositivo/$idDispositivo"
        Log.v("DEVICE REPOSITORY", "ID DISPOSITIVO -> ${idDispositivo}")

        val request = Request.Builder()
            .url(url)
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

                            // Converte o jsonObject em um objeto Dispositivo
                            val dispositivo = Dispositivo(
                                id_dispositivo = jsonObject.getInt("id_dispositivo"),
                                id_ambiente = jsonObject.getInt("id_ambiente"),
                                nome = jsonObject.getString("nome"),
                                consumo_medio = jsonObject.getDouble("consumo_medio"),
                                status = jsonObject.getString("status"),
                                consumo_energetico = mutableListOf() // Adapte conforme necessário
                            )

                            callback(dispositivo, null)
                        } catch (e: Exception) {
                            callback(null, "Erro ao processar a resposta do servidor")
                        }
                    } else {
                        callback(null, "Resposta vazia do servidor")
                    }
                } else {
                    callback(null, "Erro ao buscar dispositivo")
                }
            }
        })
    }

    fun update(dispositivo: Dispositivo, callback: (JSONObject?, String?) -> Unit) {
        val url = "$BASE_URL/dispositivo/${dispositivo.id_dispositivo}"

        val json = JSONObject().apply {
            put("nome", dispositivo.nome)
            put("consumo_medio", dispositivo.consumo_medio)
            put("status", dispositivo.status)
        }

        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .put(body)
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
                    callback(null, "Erro ao atualizar dispositivo")
                }
            }
        })
    }

    fun delete(idDispositivo: Int, callback: (Boolean, String?) -> Unit) {
        val url = "$BASE_URL/dispositivo/$idDispositivo"

        val request = Request.Builder()
            .url(url)
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, "Falha ao conectar ao servidor")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(true, null)  // Excluído com sucesso
                } else {
                    callback(false, "Erro ao excluir dispositivo: ${response.message}")
                }
            }
        })
    }


}
