package com.example.chargetech.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.chargetech.R
import com.example.chargetech.activities.ProfileActivity
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

            deleteButton.setOnClickListener {
                showDeleteConfirmationDialog(consumoEnergetico)
            }
        }

        private fun showDeleteConfirmationDialog(consumoEnergetico: ConsumoEnergetico) {
            val context = itemView.context
            val alertDialog = AlertDialog.Builder(context)
                .setTitle("Excluir consumo energético")
                .setMessage("Você tem certeza que deseja excluir este consumo?")
                .setPositiveButton("Sim") { dialog, _ ->
                    consumoEnergeticoRepository.deleteById(consumoEnergetico.id_consumo_energetico) { success ->
                        if (success) {
                            val position = adapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                consumos.removeAt(position)
                                notifyItemRemoved(position)
                            }
                            val profileIntent = Intent(context, ProfileActivity::class.java)
                            context.startActivity(profileIntent)
                            Toast.makeText(context, "Consumo excluído com sucesso", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Erro ao excluir consumo", Toast.LENGTH_SHORT).show()
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Não") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            alertDialog.show()
        }
    }
}
