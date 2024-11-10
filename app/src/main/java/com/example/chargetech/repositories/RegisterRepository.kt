package com.example.chargetech.repositories

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class RegisterRepository () {

    private val client = OkHttpClient()
    private val BASE_URL = "https://569586ab-db4d-4447-b516-9032818e8306-00-25w7cq51q41rq.janeway.replit.dev"

    fun register(
        nome: String,
        email: String,
        data_nascimento: String,
        senha: String,
        id_genero: Int?,
        callback: (Boolean, String?) -> Unit)
    {
        val jsonBody = """
            {
                "nome": "${nome}",
                "email": "${email}",
                "data_nascimento": "${data_nascimento}",
                "senha": "${senha}",
                "id_genero": "${id_genero}"
            }
        """.trimIndent()

        val request = Request.Builder()
            .url("$BASE_URL/usuario")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, "Falha ao conectar ao servidor: ${e.message}")
                Log.v("REGISTRO", "${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(true, "Usuário cadastrado com sucesso!")
                } else {
                    callback(false, "Informações de cadastro inválidas")
                }
            }
        })
    }


}