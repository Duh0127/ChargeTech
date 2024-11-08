package com.example.chargetech.models

data class ConsumoEnergetico (
    val id_consumo_energetico: Int,
    val id_dispositivo: Int,
    val data_registro: String,
    val consumo: Float,
    val custo_consumo: Float,
    val custo_estimado: Float,
)