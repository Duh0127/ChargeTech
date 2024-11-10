package com.example.chargetech.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chargetech.R
import com.example.chargetech.models.Dispositivo

class DispositivoAdapter(
    private val dispositivos: List<Dispositivo>
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
        private val deviceStatus: TextView = itemView.findViewById(R.id.deviceStatus)
        private val consumoEnergeticoRecyclerView: RecyclerView = itemView.findViewById(R.id.consumoEnergeticoRecyclerView)
        private val noConsumoMessage: TextView = itemView.findViewById(R.id.noConsumoMessage)
        private var isExpanded = false

        fun bind(dispositivo: Dispositivo) {
            deviceName.text = dispositivo.nome
            deviceStatus.text = dispositivo.status

            // Configuração do adapter do RecyclerView de consumo energético
            val consumoAdapter = ConsumoEnergeticoAdapter(dispositivo.consumo_energetico)
            consumoEnergeticoRecyclerView.adapter = consumoAdapter
            consumoEnergeticoRecyclerView.layoutManager = LinearLayoutManager(itemView.context)

            // Inicialmente, escondemos a mensagem de "Não possui consumo energético"
            noConsumoMessage.visibility = View.GONE

            // Configuração do clique para expandir o card e exibir o RecyclerView
            itemView.setOnClickListener {
                isExpanded = !isExpanded
                // Quando expandido, verificamos a lista de consumos energéticos
                if (isExpanded) {
                    // Se a lista estiver vazia, exibe a mensagem
                    if (dispositivo.consumo_energetico.isEmpty()) {
                        noConsumoMessage.visibility = View.VISIBLE
                    } else {
                        consumoEnergeticoRecyclerView.visibility = View.VISIBLE
                    }
                } else {
                    consumoEnergeticoRecyclerView.visibility = View.GONE
                    noConsumoMessage.visibility = View.GONE
                }
            }
        }
    }
}



