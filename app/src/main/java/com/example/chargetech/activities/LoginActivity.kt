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
import com.example.chargetech.models.Usuario
import com.example.chargetech.repositories.LoginRepository
import org.json.JSONObject

class LoginActivity : Activity() {

    private val loginRepository = LoginRepository()

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.login_layout)

        val backToHomePageButton = findViewById<Button>(R.id.backToHomePage)
        backToHomePageButton.setOnClickListener {
            val homePageIntent = Intent(this, HomePageActivity::class.java)
            startActivity(homePageIntent)
        }

        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)

        val buttonToLogin = findViewById<Button>(R.id.buttonToLogin)
        buttonToLogin.setOnClickListener {
            val emailData = emailField.text.toString()
            val passwordData = passwordField.text.toString()

            loginRepository.login(email = emailData, password = passwordData) { jsonObject, errorMessage ->
                runOnUiThread {
                    if (jsonObject != null) {
                        try {
                            // Processar o JSON e instanciar a classe Usuario
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

                            // Salvar no SharedPreferences
                            saveUserData(usuario, token)

                            Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                            // Navegar para a próxima activity
                            val profileIntent = Intent(this@LoginActivity, ProfileActivity::class.java)
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

        val buttonToGoOnForgotPassword = findViewById<Button>(R.id.forgotPasswordButton)
        buttonToGoOnForgotPassword.setOnClickListener {
            val forgotPasswordIntent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(forgotPasswordIntent)
        }
    }

    private fun saveUserData(usuario: Usuario, token: String) {
        Log.v("Login", "${usuario}")
        Log.v("Login", "ID_USUARIO - ${usuario.id_usuario}")
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
}
