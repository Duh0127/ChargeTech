package com.example.chargetech.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chargetech.R
import com.example.chargetech.activities.NewDeviceActivity
import com.example.chargetech.activities.ProfileActivity
import com.example.chargetech.models.Dispositivo
import com.example.chargetech.repositories.DeviceRepository

class DispositivoAdapter(
    private val dispositivos: MutableList<Dispositivo>
) : RecyclerView.Adapter<DispositivoAdapter.DispositivoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DispositivoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dispositivo_card, parent, false)
        return DispositivoViewHolder(view)
    }

    override fun onBindViewHolder(holder: DispositivoViewHolder, position: Int) {
        val dispositivo = dispositivos[position]
        holder.bind(dispositivo)
    }

    override fun getItemCount(): Int = dispositivos.size

    inner class DispositivoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val deviceName: TextView = itemView.findViewById(R.id.deviceName)
        private val avgConsumption: TextView = itemView.findViewById(R.id.avgConsumption)
//        private val addConsumoButton: Button = itemView.findViewById(R.id.addConsumoButton)
        private val editDeviceButton: Button = itemView.findViewById(R.id.editDeviceButton)
        private val deleteDeviceButton: Button = itemView.findViewById(R.id.deleteDeviceButton)
        private val consumoEnergeticoRecyclerView: RecyclerView = itemView.findViewById(R.id.consumoEnergeticoRecyclerView)
        private val noConsumoMessage: TextView = itemView.findViewById(R.id.noConsumoMessage)
        private val clickToExpandText: TextView = itemView.findViewById(R.id.clickToExpandDevice)
        private var isExpanded = false

        fun bind(dispositivo: Dispositivo) {
            deviceName.text = dispositivo.nome
            avgConsumption.text = "Consumo médio: ${dispositivo.consumo_medio}KW/h"

            val consumoAdapter = ConsumoEnergeticoAdapter(dispositivo.consumo_energetico)
            consumoEnergeticoRecyclerView.adapter = consumoAdapter
            consumoEnergeticoRecyclerView.layoutManager = LinearLayoutManager(itemView.context)

            noConsumoMessage.visibility = View.GONE

            itemView.setOnClickListener {
                isExpanded = !isExpanded
                if (isExpanded) {
                    clickToExpandText.visibility = View.GONE
                    if (dispositivo.consumo_energetico.isEmpty()) {
                        noConsumoMessage.visibility = View.VISIBLE
                    } else {
                        consumoEnergeticoRecyclerView.visibility = View.VISIBLE
                    }
                } else {
                    consumoEnergeticoRecyclerView.visibility = View.GONE
                    noConsumoMessage.visibility = View.GONE
                    clickToExpandText.visibility = View.VISIBLE
                }
            }

//            addConsumoButton.setOnClickListener {
//                val context = itemView.context
//                val intent = Intent(context, NewEnergyConsumptionActivity::class.java)
//                intent.putExtra("id_dispositivo", dispositivo.id_dispositivo)
//                context.startActivity(intent)
//            }

            editDeviceButton.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, NewDeviceActivity::class.java)
                intent.putExtra("id_dispositivo", dispositivo.id_dispositivo)
                context.startActivity(intent)
            }

            deleteDeviceButton.setOnClickListener {
                val context = itemView.context
                val dispositivoId = dispositivo.id_dispositivo

                val alertDialog = android.app.AlertDialog.Builder(context)
                    .setTitle("Excluir dispositivo")
                    .setMessage("Você tem certeza que deseja excluir este dispositivo?")
                    .setPositiveButton("Sim") { dialog, _ ->
                        val deviceRepository = DeviceRepository()
                        deviceRepository.delete(dispositivoId) { success, errorMessage ->
                            if (success) {
                                dispositivos.removeAt(bindingAdapterPosition)
                                var profileIntent = Intent(context, ProfileActivity::class.java)
                                context.startActivity(profileIntent)
                            } else {
                                android.widget.Toast.makeText(context, errorMessage ?: "Erro desconhecido", android.widget.Toast.LENGTH_SHORT).show()
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
}



