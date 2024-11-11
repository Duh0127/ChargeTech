package com.example.chargetech.models

data class Dispositivo (
    val id_dispositivo: Int,
    val id_ambiente: Int,
    val nome: String,
    val consumo_medio: Double,
    val status: String,
    val consumo_energetico: List<ConsumoEnergetico> = emptyList()
)