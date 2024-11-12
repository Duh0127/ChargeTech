package com.example.chargetech.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
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

        val title = findViewById<TextView>(R.id.titleEnvironment)
        val nome = findViewById<EditText>(R.id.environmentName)
        val descricao = findViewById<EditText>(R.id.environmentDescription)
        val registerButton = findViewById<Button>(R.id.addNewEnvironmentButton)
        val progressBar = findViewById<ProgressBar>(R.id.loadingProgressBar)

        val idAmbiente = intent.getIntExtra("id_ambiente", -1)

        if (idAmbiente != -1) {
            title.setText("Atualizar Ambiente")
            registerButton.setText("Atualizar")
            ambienteRepository.getById(idAmbiente) { ambiente, errorMessage ->
                runOnUiThread {
                    if (ambiente != null) {
                        nome.setText(ambiente.nome)
                        descricao.setText(ambiente.descricao)
                    } else {
                        Toast.makeText(this, "Erro ao buscar ambiente: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        registerButton.setOnClickListener {
            val nomeInput = nome.text.toString()
            val descricaoInput = descricao.text.toString()

            // Exibir o ProgressBar e esconder o botão
            registerButton.visibility = Button.GONE
            progressBar.visibility = ProgressBar.VISIBLE

            if (idAmbiente == -1) {
                ambienteRepository.register(nome = nomeInput, descricao = descricaoInput, id_usuario = id_usuario) { jsonObject, errorMessage ->
                    runOnUiThread {
                        // Esconder o ProgressBar e mostrar o botão novamente
                        progressBar.visibility = ProgressBar.GONE
                        registerButton.visibility = Button.VISIBLE

                        if (jsonObject != null) {
                            Toast.makeText(this, "Ambiente cadastrado com sucesso!", Toast.LENGTH_LONG).show()
                            val profileIntent = Intent(this@NewEnvironmentActivity, ProfileActivity::class.java)
                            startActivity(profileIntent)
                            finish()
                        } else {
                            Toast.makeText(this, errorMessage ?: "Erro ao criar ambiente", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                ambienteRepository.update(idAmbiente, nome = nomeInput, descricao = descricaoInput) { jsonObject, errorMessage ->
                    runOnUiThread {
                        // Esconder o ProgressBar e mostrar o botão novamente
                        progressBar.visibility = ProgressBar.GONE
                        registerButton.visibility = Button.VISIBLE

                        if (jsonObject != null) {
                            Toast.makeText(this, "Ambiente atualizado com sucesso!", Toast.LENGTH_LONG).show()
                            val profileIntent = Intent(this@NewEnvironmentActivity, ProfileActivity::class.java)
                            startActivity(profileIntent)
                            finish()
                        } else {
                            Toast.makeText(this, errorMessage ?: "Erro ao atualizar ambiente", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}
