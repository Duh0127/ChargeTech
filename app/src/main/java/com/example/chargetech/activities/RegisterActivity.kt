package com.example.chargetech.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.chargetech.R
import com.example.chargetech.repositories.RegisterRepository
import java.util.regex.Pattern

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
        val nameField = findViewById<EditText>(R.id.nameField)
        val emailField = findViewById<EditText>(R.id.emailField)
        val dateField = findViewById<EditText>(R.id.dateField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
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

        // Máscara para o campo de data de nascimento
        dateField.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            private val mask = "##/##/####"
            private var oldText = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) {
                    oldText = s.toString()
                    isUpdating = false
                    return
                }

                // Remove caracteres não numéricos
                var newText = s.toString().replace(Regex("[^0-9]"), "")

                // Limita o tamanho máximo para 8 dígitos (DDMMYYYY)
                if (newText.length > 8) newText = newText.substring(0, 8)

                // Aplica a máscara
                var formattedText = ""
                var i = 0
                mask.forEach { c ->
                    if (c == '#' && i < newText.length) {
                        formattedText += newText[i]
                        i++
                    } else if (i < newText.length) {
                        formattedText += c
                    }
                }

                // Atualiza o campo de forma segura
                isUpdating = true
                dateField.setText(formattedText)
                dateField.setSelection(formattedText.length)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Validação para o botão de cadastro
        registerButton.setOnClickListener {
            val nameData = nameField.text.toString()
            val emailData = emailField.text.toString()
            val dateData = dateField.text.toString()
            val passwordData = passwordField.text.toString()

            if (validateInputs(nameData, emailData, dateData, passwordData)) {
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
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(name: String, email: String, date: String, password: String): Boolean {
        return name.isNotEmpty() && email.isNotEmpty() && date.isNotEmpty() && password.isNotEmpty() && idGeneroSelecionado != null
    }

    private fun handleSuccessfulRegistration() {
        Log.d("CADASTRO", "Cadastro Efetuado com sucesso")

        val nameField = findViewById<EditText>(R.id.nameField)
        val emailField = findViewById<EditText>(R.id.emailField)
        val dateField = findViewById<EditText>(R.id.dateField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
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
