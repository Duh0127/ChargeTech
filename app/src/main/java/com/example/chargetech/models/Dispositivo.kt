package com.example.chargetech.models

data class Dispositivo (
    val id_dispositivo: Int,
    val id_ambiente: Int,
    val nome: String,
    val imagem: String,
    val consumo_medio: Float,
    val status: String,
)