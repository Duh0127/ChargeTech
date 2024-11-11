package com.example.chargetech.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.chargetech.R
import com.example.chargetech.models.Ambiente
import com.example.chargetech.models.Usuario
import com.example.chargetech.repositories.AmbienteRepository
import com.example.chargetech.repositories.TokenRepository

class NewEnvironmentActivity : Activity() {

    private val ambienteRepository = AmbienteRepository()

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.new_environment_layout)

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val id_usuario = sharedPreferences.getInt("id_usuario", 0)

        val buttonToBack = findViewById<Button>(R.id.backToProfileFromNewEnvironment)
        buttonToBack.setOnClickListener {
            var profileIntent = Intent(this@NewEnvironmentActivity, ProfileActivity::class.java)
            startActivity(profileIntent)
        }

        var nome = findViewById<EditText>(R.id.environmentName)
        var descricao = findViewById<EditText>(R.id.environmentDescription)

        val registerButton = findViewById<Button>(R.id.addNewEnvironmentButton)
        registerButton.setOnClickListener {
            ambienteRepository.register(nome = nome.text.toString(), descricao = descricao.text.toString(), id_usuario = id_usuario) { jsonObject, errorMessage ->
                runOnUiThread {
                    if (jsonObject != null) {
                        try {
                            Toast.makeText(this, "Ambiente cadastrado com Sucesso!", Toast.LENGTH_LONG).show()
                            val profileIntent = Intent(this@NewEnvironmentActivity, ProfileActivity::class.java)
                            startActivity(profileIntent)
                            finish()
                        } catch (e: Exception) {
                            Toast.makeText(this, "Erro ao processar dados do usuário", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, errorMessage ?: "Erro ao realizar login", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }





    }








}