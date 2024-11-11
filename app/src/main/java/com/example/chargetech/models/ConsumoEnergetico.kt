package com.example.chargetech.models

data class ConsumoEnergetico (
    val id_consumo_energetico: Int,
    val id_dispositivo: Int,
    val data_registro: String,
    val consumo: Double
)