package com.example.chargetech.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chargetech.R
import com.example.chargetech.models.ConsumoEnergetico

class ConsumoEnergeticoAdapter(
    private val consumos: List<ConsumoEnergetico>
) : RecyclerView.Adapter<ConsumoEnergeticoAdapter.ConsumoEnergeticoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsumoEnergeticoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.consumo_energetico_card, parent, false)
        return ConsumoEnergeticoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsumoEnergeticoViewHolder, position: Int) {
        val consumo = consumos[position]
        holder.bind(consumo)
    }

    override fun getItemCount(): Int = consumos.size

    inner class ConsumoEnergeticoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dataRegistro: TextView = itemView.findViewById(R.id.dataRegistro)
        private val consumo: TextView = itemView.findViewById(R.id.consumo)

        fun bind(consumoEnergetico: ConsumoEnergetico) {
            dataRegistro.text = consumoEnergetico.data_registro
            consumo.text = "${consumoEnergetico.consumo} kWh"
        }
    }
}
