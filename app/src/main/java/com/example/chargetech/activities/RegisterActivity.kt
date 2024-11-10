package com.example.chargetech.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import com.example.chargetech.R
import com.example.chargetech.repositories.RegisterRepository

class RegisterActivity : Activity() {

    private val registerRepository = RegisterRepository()
    private var idGeneroSelecionado: Int? = null

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.register_layout)

        val buttonToBackToHomePage = findViewById<Button>(R.id.backToHomePageFromRegister)
        buttonToBackToHomePage.setOnClickListener {
            val homePageIntent = Intent(this, HomePageActivity::class.java)
            startActivity(homePageIntent)
        }

        val buttonToLoginActivity = findViewById<Button>(R.id.buttonToAccessLoginActivity)
        buttonToLoginActivity.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }


        val registerButton = findViewById<Button>(R.id.buttonToRegister)
        var nameField = findViewById<EditText>(R.id.nameField)
        var emailField = findViewById<EditText>(R.id.emailField)
        var dateField = findViewById<EditText>(R.id.dateField)
        var passwordField = findViewById<EditText>(R.id.passwordField)
        val radioGroupGenero = findViewById<RadioGroup>(R.id.radioGroupGenero)

        // Listener para capturar o ID do gênero selecionado
        radioGroupGenero.setOnCheckedChangeListener { group, checkedId ->
            idGeneroSelecionado = when (checkedId) {
                R.id.radioMasculino -> 1
                R.id.radioFeminino -> 2
                R.id.radioNenhum -> 3
                else -> null
            }
        }

        registerButton.setOnClickListener {
            val nameData = nameField.text.toString()
            val emailData = emailField.text.toString()
            val dateData = dateField.text.toString()
            val passwordData = passwordField.text.toString()

            registerRepository.register(
                nome = nameData,
                email = emailData,
                data_nascimento = dateData,
                senha = passwordData,
                id_genero = idGeneroSelecionado
            ) { success, errorMessage ->
                runOnUiThread {
                    if (success) {
                        handleSuccessfulRegistration()
                    } else {
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()




    }


    private fun handleSuccessfulRegistration() {
        Log.d("CADASTRO", "Cadastro Efetuado com sucesso")

        var nameField = findViewById<EditText>(R.id.nameField)
        var emailField = findViewById<EditText>(R.id.emailField)
        var dateField = findViewById<EditText>(R.id.dateField)
        var passwordField = findViewById<EditText>(R.id.passwordField)
        val radioGroupGenero = findViewById<RadioGroup>(R.id.radioGroupGenero)

        nameField.setText("")
        emailField.setText("")
        dateField.setText("")
        passwordField.setText("")
        radioGroupGenero.clearCheck()

        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show()

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        finish()
    }


}