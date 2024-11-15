package com.example.chargetech.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.example.chargetech.R

class HomePageActivity : Activity() {
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.homepage_layout)
        setLogoImage()
        loginButtonAction()
        registerButtonAction()
    }

    override fun onStart() {
        super.onStart()
        verifyIfIsLogged()
    }



    // FUNCOES

    private fun setLogoImage() {
        val logoChargeTech = findViewById<ImageView>(R.id.logo_chargetech)
        logoChargeTech.setImageDrawable(resources.getDrawable(R.drawable.logo_chargetech, theme))
    }

    private fun registerButtonAction() {
        val registerButton = findViewById<Button>(R.id.register_button)
        registerButton.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }
    }

    private fun loginButtonAction() {
        val loginButton = findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    private fun verifyIfIsLogged() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        if (token != null) {
            val profileIntent = Intent(this, ProfileActivity::class.java)
            startActivity(profileIntent)
            finish()
        }
    }
}