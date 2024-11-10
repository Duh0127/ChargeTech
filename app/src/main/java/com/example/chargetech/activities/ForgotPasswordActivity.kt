package com.example.chargetech.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.chargetech.R

class ForgotPasswordActivity : Activity() {


    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.forgot_password_layout)

        val buttonToBackToLoginActivity = findViewById<Button>(R.id.backToLoginPage)
        buttonToBackToLoginActivity.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        val buttonToForgotPassword = findViewById<Button>(R.id.buttonToForgotPassword)



    }



}