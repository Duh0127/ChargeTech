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
import com.example.chargetech.models.Dispositivo
import com.example.chargetech.repositories.DeviceRepository

class NewDeviceActivity : Activity() {

    private val deviceRepository = DeviceRepository()

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.new_device_layout)

        backToProfileActivity()

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val registerButton = findViewById<Button>(R.id.addNewDeviceButton)

        val title = findViewById<TextView>(R.id.titleNewDevice)
        val nomeField = findViewById<EditText>(R.id.deviceNameField)
        val consumptionField = findViewById<EditText>(R.id.deviceConsumptionField)
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val id_ambiente = sharedPreferences.getInt("selected_environment_id", -1)
        val idDispositivo = intent.getIntExtra("id_dispositivo", -1)

        if (idDispositivo != -1) {
            title.setText("Atualizar Dispositivo")
            registerButton.setText("Atualizar")
            deviceRepository.getById(idDispositivo) { dispositivo, errorMessage ->
                runOnUiThread {
                    if (dispositivo != null) {
                        nomeField.setText(dispositivo.nome)
                        consumptionField.setText(dispositivo.consumo_medio.toString())
                    } else {
                        Toast.makeText(this, errorMessage ?: "Erro ao carregar dispositivo", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        registerButton.setOnClickListener {
            val nome = nomeField.text.toString()
            val consumoMedio = consumptionField.text.toString().toDoubleOrNull()

            if (nome.isEmpty() || consumoMedio == null) {
                Toast.makeText(this, "Por favor, preencha todos os campos corretamente", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (id_ambiente == -1) {
                Toast.makeText(this, "Ambiente não selecionado", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val dispositivo = Dispositivo(
                id_dispositivo = idDispositivo,
                id_ambiente = id_ambiente,
                nome = nome,
                consumo_medio = consumoMedio,
                status = "Ativo",
                consumo_energetico = mutableListOf()
            )

            registerButton.visibility = Button.GONE
            progressBar.visibility = ProgressBar.VISIBLE

            if (idDispositivo == -1) {
                deviceRepository.register(dispositivo) { jsonObject, errorMessage ->
                    runOnUiThread {
                        registerButton.visibility = Button.VISIBLE
                        progressBar.visibility = ProgressBar.GONE
                        if (jsonObject != null) {
                            Toast.makeText(this, "Dispositivo criado com sucesso!", Toast.LENGTH_LONG).show()
                            val profileIntent = Intent(this@NewDeviceActivity, ProfileActivity::class.java)
                            startActivity(profileIntent)
                            finish()
                        } else {
                            Toast.makeText(this, errorMessage ?: "Erro ao criar dispositivo", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                deviceRepository.update(dispositivo) { jsonObject, errorMessage ->
                    runOnUiThread {
                        registerButton.visibility = Button.VISIBLE
                        progressBar.visibility = ProgressBar.GONE
                        if (jsonObject != null) {
                            Toast.makeText(this, "Dispositivo atualizado com sucesso!", Toast.LENGTH_LONG).show()
                            val profileIntent = Intent(this@NewDeviceActivity, ProfileActivity::class.java)
                            startActivity(profileIntent)
                            finish()
                        } else {
                            Toast.makeText(this, errorMessage ?: "Erro ao atualizar dispositivo", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }


    private fun backToProfileActivity() {
        val buttonToBack = findViewById<Button>(R.id.backToProfileFromNewDevice)
        buttonToBack.setOnClickListener {
            val profileIntent = Intent(this@NewDeviceActivity, ProfileActivity::class.java)
            startActivity(profileIntent)
        }
    }

}
