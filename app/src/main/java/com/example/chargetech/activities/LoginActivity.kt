package com.example.chargetech.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.chargetech.R
import com.example.chargetech.models.Usuario
import com.example.chargetech.repositories.LoginRepository

class LoginActivity : Activity() {
    private val loginRepository = LoginRepository()

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.login_layout)
        backToHomePageButton()
        createNewAccountButton()

        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val buttonToLogin = findViewById<Button>(R.id.buttonToLogin)

        buttonToLogin.setOnClickListener {
            val emailData = emailField.text.toString()
            val passwordData = passwordField.text.toString()

            if (emailData.isEmpty() || passwordData.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos corretamente", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!isValidEmail(emailData)) {
                Toast.makeText(this, "Por favor, insira um e-mail válido", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            loginRepository.login(email = emailData, password = passwordData) { jsonObject, errorMessage ->
                runOnUiThread {
                    if (jsonObject != null) {
                        try {
                            val message = jsonObject.getString("message")
                            val usuarioJson = jsonObject.getJSONObject("usuario")
                            val token = jsonObject.getString("token")

                            val usuario = Usuario(
                                id_usuario = usuarioJson.getInt("id_usuario"),
                                id_genero = usuarioJson.getJSONObject("genero").getInt("id_genero"),
                                nome = usuarioJson.getString("nome"),
                                email = usuarioJson.getString("email"),
                                data_nascimento = usuarioJson.getString("data_nascimento"),
                                senha = ""
                            )

                            Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                            saveUserDataOnSharedPreferences(usuario, token)
                            accessProfileActivity()
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

    private fun saveUserDataOnSharedPreferences(usuario: Usuario, token: String) {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("id_usuario", usuario.id_usuario)
            putInt("id_genero", usuario.id_genero)
            putString("nome", usuario.nome)
            putString("email", usuario.email)
            putString("data_nascimento", usuario.data_nascimento)
            putString("senha", usuario.senha)
            putString("token", token)
            apply()
        }
    }

    private fun backToHomePageButton() {
        val backToHomePageButton = findViewById<Button>(R.id.backToHomePage)
        backToHomePageButton.setOnClickListener {
            val homePageIntent = Intent(this, HomePageActivity::class.java)
            startActivity(homePageIntent)
        }
    }

    private fun createNewAccountButton() {
        val createNewAccountButton = findViewById<Button>(R.id.createNewAccountButton)
        createNewAccountButton.setOnClickListener {
            val createNewAccountIntent = Intent(this, RegisterActivity::class.java)
            startActivity(createNewAccountIntent)
        }
    }

    private fun accessProfileActivity() {
        val profileIntent = Intent(this@LoginActivity, ProfileActivity::class.java)
        startActivity(profileIntent)
        finish()
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
