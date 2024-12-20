package com.example.chargetech.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.chargetech.R
import com.example.chargetech.models.ConsumoEnergetico
import com.example.chargetech.repositories.ConsumoEnergeticoRepository

class NewEnergyConsumptionActivity : Activity() {

    private val consumoEnergeticoRepository = ConsumoEnergeticoRepository()

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.new_consumption_layout)
        backToProfileButton()

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val consumptionField = findViewById<EditText>(R.id.consumptionField)

        val registerButton = findViewById<Button>(R.id.addNewConsumptionButton)
        registerButton.setOnClickListener {
            val consumptionFieldText = consumptionField.text.toString()
            if (consumptionFieldText.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha o consumo", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val id_dispositivo = intent.getIntExtra("id_dispositivo", 0)
            val consumoData = consumptionField.text.toString().toDouble()
            val consumoEnergetico = ConsumoEnergetico(
                id_dispositivo = id_dispositivo,
                consumo = consumoData,
                data_registro = "",
                id_consumo_energetico = 0
            )

            registerButton.visibility = Button.GONE
            progressBar.visibility = ProgressBar.VISIBLE

            consumoEnergeticoRepository.register(consumoEnergetico) { jsonObject, errorMessage ->
                runOnUiThread {
                    registerButton.visibility = Button.VISIBLE
                    progressBar.visibility = ProgressBar.GONE
                    if (jsonObject != null) {
                        try {
                            Toast.makeText(
                                this,
                                "Consumo cadastrado com Sucesso!",
                                Toast.LENGTH_LONG
                            ).show()
                            val profileIntent = Intent(this@NewEnergyConsumptionActivity, ProfileActivity::class.java)
                            startActivity(profileIntent)
                            finish()
                        } catch (e: Exception) {
                            Toast.makeText(this, "Erro ao processar dados", Toast.LENGTH_LONG)
                                .show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            errorMessage ?: "Erro ao realizar login",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }


    private fun backToProfileButton() {
        val buttonToBack = findViewById<Button>(R.id.backToProfileFromNewConsumption)
        buttonToBack.setOnClickListener {
            val profileIntent = Intent(this@NewEnergyConsumptionActivity, ProfileActivity::class.java)
            startActivity(profileIntent)
            finish()
        }
    }

}