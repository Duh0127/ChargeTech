package com.example.chargetech.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.chargetech.R
import com.example.chargetech.models.ConsumoEnergetico
import com.example.chargetech.repositories.ConsumoEnergeticoRepository

class ConsumoEnergeticoAdapter(
    private val consumos: MutableList<ConsumoEnergetico>
) : RecyclerView.Adapter<ConsumoEnergeticoAdapter.ConsumoEnergeticoViewHolder>() {

    private val consumoEnergeticoRepository = ConsumoEnergeticoRepository()

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
        private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        private val consumo: TextView = itemView.findViewById(R.id.consumo)

        fun bind(consumoEnergetico: ConsumoEnergetico) {
            dataRegistro.text = consumoEnergetico.data_registro
            consumo.text = "${consumoEnergetico.consumo} kWh"

            // Configura o botão de deletar
            deleteButton.setOnClickListener {
                consumoEnergeticoRepository.deleteById(consumoEnergetico.id_consumo_energetico) { success ->
                    if (success) {
                        // Remove o item da lista e notifica o adapter
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            consumos.removeAt(position)
                            notifyItemRemoved(position)
                        }
                        Toast.makeText(itemView.context, "Consumo excluído com sucesso", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(itemView.context, "Erro ao excluir consumo", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
