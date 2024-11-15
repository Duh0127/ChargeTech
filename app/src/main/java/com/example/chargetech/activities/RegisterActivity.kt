package com.example.chargetech.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
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
        backToHomePageButton()
        buttonToAccessLoginActivity()

        val registerButton = findViewById<Button>(R.id.buttonToRegister)
        val nameField = findViewById<EditText>(R.id.nameField)
        val emailField = findViewById<EditText>(R.id.emailField)
        val dateField = findViewById<EditText>(R.id.dateField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val radioGroupGenero = findViewById<RadioGroup>(R.id.radioGroupGenero)

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
                var newText = s.toString().replace(Regex("[^0-9]"), "")
                if (newText.length > 8) newText = newText.substring(0, 8)

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

                isUpdating = true
                dateField.setText(formattedText)
                dateField.setSelection(formattedText.length)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

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
                        if (success) handleSuccessfulRegistration()
                        else Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun validateInputs(name: String, email: String, date: String, password: String): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(this, "Por favor, insira o nome.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor, insira o e-mail.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor, insira um e-mail válido.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (date.isEmpty()) {
            Toast.makeText(this, "Por favor, insira a data de nascimento.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Por favor, insira a senha.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 5) {
            Toast.makeText(this, "A senha deve ter pelo menos 5 caracteres.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!password.any { it.isUpperCase() }) {
            Toast.makeText(this, "A senha deve conter pelo menos uma letra maiúscula.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (idGeneroSelecionado == null) {
            Toast.makeText(this, "Por favor, selecione um gênero.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
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

    private fun backToHomePageButton() {
        val buttonToBackToHomePage = findViewById<Button>(R.id.backToHomePageFromRegister)
        buttonToBackToHomePage.setOnClickListener {
            val homePageIntent = Intent(this, HomePageActivity::class.java)
            startActivity(homePageIntent)
        }
    }

    private fun buttonToAccessLoginActivity() {
        val buttonToLoginActivity = findViewById<Button>(R.id.buttonToAccessLoginActivity)
        buttonToLoginActivity.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }
}
