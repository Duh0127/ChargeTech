package com.example.chargetech.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chargetech.R
import com.example.chargetech.adapters.AmbienteAdapter
import com.example.chargetech.models.Ambiente
import com.example.chargetech.models.ConsumoEnergetico
import com.example.chargetech.models.Dispositivo
import com.example.chargetech.repositories.ProfileRepository
import com.example.chargetech.repositories.TokenRepository
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileActivity : Activity() {

    private val tokenRepository = TokenRepository()
    private val profileRepository = ProfileRepository()

    private var dataLoaded = false

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.profile_layout)

        val profileImg = findViewById<ImageView>(R.id.profileImage)
        profileImg.setImageResource(R.drawable.logo_chargetech)

        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            val homePageIntent = Intent(this@ProfileActivity, HomePageActivity::class.java)
            startActivity(homePageIntent)
            finish()
        }

        val addNewEnvironmentButton = findViewById<Button>(R.id.addNewEnvironmentButton)
        addNewEnvironmentButton.setOnClickListener {
            val newEnvironmentActivity = Intent(this@ProfileActivity, NewEnvironmentActivity::class.java)
            startActivity(newEnvironmentActivity)
        }
    }

    override fun onStart() {
        super.onStart()

        val profileLayout = findViewById<LinearLayout>(R.id.profile_layout)
        profileLayout.visibility = LinearLayout.GONE

        val progressBar = findViewById<ProgressBar>(R.id.loadingProgressBar)
        progressBar.visibility = ProgressBar.VISIBLE

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "N/A")
        Log.v("ProfileActivity", "Token: $token")

        if (token != null) {
            tokenRepository.decodeToken(token) { jsonObject, errorMessage ->
                runOnUiThread {
                    if (jsonObject != null) {
                        val id_usuario = jsonObject.getJSONObject("decodedToken").getInt("id_usuario")
                        profileRepository.getUserById(id_usuario) { userProfileJson, profileErrorMessage ->
                            runOnUiThread {
                                if (userProfileJson != null) {
                                    var nome = userProfileJson.getString("nome")
                                    var email = userProfileJson.getString("email")
                                    var dataNascimento = userProfileJson.getString("data_nascimento")
                                    nome = formatName(nome)

                                    val userNameText = findViewById<TextView>(R.id.userNameText)
                                    val userEmailText = findViewById<TextView>(R.id.userEmailText)
                                    val userBornText = findViewById<TextView>(R.id.userBornText)

                                    userNameText.text = nome
                                    userEmailText.text = email
                                    userBornText.text = dataNascimento

                                    val ambientes = parseAmbientes(userProfileJson)
                                    fetchUserAmbientes(ambientes)

                                    dataLoaded = true
                                    hideLoading(progressBar, profileLayout)
                                } else {
                                    Toast.makeText(this, profileErrorMessage, Toast.LENGTH_LONG).show()
                                    dataLoaded = true
                                    hideLoading(progressBar, profileLayout)
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                        dataLoaded = true
                        hideLoading(progressBar, profileLayout)
                    }
                }
            }
        } else {
            Toast.makeText(this, "Token não encontrado", Toast.LENGTH_LONG).show()
            progressBar.visibility = ProgressBar.GONE
            val homePageIntent = Intent(this@ProfileActivity, HomePageActivity::class.java)
            startActivity(homePageIntent)
            finish()
        }
    }

    private fun hideLoading(progressBar: ProgressBar, profileLayout: LinearLayout) {
        if (dataLoaded) {
            progressBar.visibility = ProgressBar.GONE
            profileLayout.visibility = LinearLayout.VISIBLE
        }
    }

    private fun parseAmbientes(userProfileJson: JSONObject): MutableList<Ambiente> {
        val ambientes = mutableListOf<Ambiente>()
        val ambientesJsonArray = userProfileJson.getJSONArray("ambientes")
        for (i in 0 until ambientesJsonArray.length()) {
            val ambienteJson = ambientesJsonArray.getJSONObject(i)

            val dispositivos = mutableListOf<Dispositivo>()
            val dispositivosJsonArray = ambienteJson.getJSONArray("dispositivos")
            for (j in 0 until dispositivosJsonArray.length()) {
                val dispositivoJson = dispositivosJsonArray.getJSONObject(j)

                val consumoEnergetico = mutableListOf<ConsumoEnergetico>()
                val consumoJsonArray = dispositivoJson.getJSONArray("consumo_energetico")
                for (k in 0 until consumoJsonArray.length()) {
                    val consumoJson = consumoJsonArray.getJSONObject(k)
                    val consumo = ConsumoEnergetico(
                        id_consumo_energetico = consumoJson.getInt("id_consumo_energetico"),
                        id_dispositivo = consumoJson.getInt("id_dispositivo"),
                        data_registro = formatDate(consumoJson.getString("data_registro")),
                        consumo = consumoJson.getDouble("consumo")
                    )
                    consumoEnergetico.add(consumo)
                }

                val dispositivo = Dispositivo(
                    id_dispositivo = dispositivoJson.getInt("id_dispositivo"),
                    id_ambiente = dispositivoJson.getInt("id_ambiente"),
                    nome = dispositivoJson.getString("nome"),
                    consumo_medio = dispositivoJson.getDouble("consumo_medio"),
                    status = dispositivoJson.getString("status"),
                    consumo_energetico = consumoEnergetico
                )
                dispositivos.add(dispositivo)
            }

            val ambiente = Ambiente(
                id_ambiente = ambienteJson.getInt("id_ambiente"),
                id_usuario = 0,
                nome = ambienteJson.getString("nome"),
                descricao = ambienteJson.getString("descricao"),
                dispositivos = dispositivos
            )
            ambientes.add(ambiente)
        }
        return ambientes
    }

    private fun fetchUserAmbientes(ambientes: MutableList<Ambiente>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewEnvironments)
        val noEnvironmentsMessage = findViewById<TextView>(R.id.noEnvironmentsMessage)

        if (ambientes.isEmpty()) {
            noEnvironmentsMessage.visibility = TextView.VISIBLE
            recyclerView.visibility = RecyclerView.GONE
        } else {
            noEnvironmentsMessage.visibility = TextView.GONE
            recyclerView.visibility = RecyclerView.VISIBLE

            recyclerView.layoutManager = LinearLayoutManager(this)
            val ambienteAdapter = AmbienteAdapter(ambientes) { ambiente ->
                val newDeviceIntent = Intent(this@ProfileActivity, NewDeviceActivity::class.java)
                startActivity(newDeviceIntent)
            }
            recyclerView.adapter = ambienteAdapter
        }
    }


    private fun formatName(name: String): String {
        val nameParts = name.split(" ")
        return when (nameParts.size) {
            1 -> nameParts[0].capitalize()
            2 -> "${nameParts[0].capitalize()} ${nameParts[1].capitalize()}"
            else -> {
                val firstName = nameParts[0].capitalize()
                val lastName = nameParts[nameParts.size - 1].capitalize()
                "$firstName $lastName"
            }
        }
    }

    private fun formatDate(dateString: String): String {
        try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy:HH-mm-ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            return if (date != null) outputFormat.format(date) else dateString
        } catch (e: Exception) {
            e.printStackTrace()
            return dateString
        }
    }
}

