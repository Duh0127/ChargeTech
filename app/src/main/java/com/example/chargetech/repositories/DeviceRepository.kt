package com.example.chargetech.repositories

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
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
}
