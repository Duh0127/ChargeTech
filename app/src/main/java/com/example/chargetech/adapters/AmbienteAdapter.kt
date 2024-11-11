package com.example.chargetech.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chargetech.R
import com.example.chargetech.models.Ambiente
import com.example.chargetech.models.Dispositivo

class AmbienteAdapter(
    private val ambientes: List<Ambiente>,
    private val onAddDeviceClick: (Ambiente) -> Unit
) : RecyclerView.Adapter<AmbienteAdapter.AmbienteViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmbienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ambiente_card, parent, false)
        return AmbienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: AmbienteViewHolder, position: Int) {
        val ambiente = ambientes[position]
        holder.bind(ambiente, expandedPositions.contains(position))

        holder.itemView.setOnClickListener {
            if (expandedPositions.contains(position)) {
                expandedPositions.remove(position)
            } else {
                expandedPositions.add(position)
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = ambientes.size

    inner class AmbienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val environmentName: TextView = itemView.findViewById(R.id.environmentName)
        private val environmentDescription: TextView = itemView.findViewById(R.id.environmentDescription)
        private val devicesList: RecyclerView = itemView.findViewById(R.id.devicesList)
        private val noDevicesMessage: TextView = itemView.findViewById(R.id.noDevicesMessage)
        private val addNewDeviceButton: Button = itemView.findViewById(R.id.addNewDeviceButton)

        fun bind(ambiente: Ambiente, isExpanded: Boolean) {
            environmentName.text = ambiente.nome
            environmentDescription.text = ambiente.descricao
            devicesList.layoutManager = LinearLayoutManager(itemView.context)
            devicesList.adapter = DispositivoAdapter(ambiente.dispositivos)

            // Mostra a mensagem 'Não possui dispositivos' se a lista estiver vazia
            if (ambiente.dispositivos.isEmpty()) {
                noDevicesMessage.visibility = View.VISIBLE
                devicesList.visibility = View.GONE
            } else {
                noDevicesMessage.visibility = View.GONE
                devicesList.visibility = if (isExpanded) View.VISIBLE else View.GONE
            }

            addNewDeviceButton.setOnClickListener {
                val sharedPreferences = itemView.context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().putInt("selected_environment_id", ambiente.id_ambiente).apply()

                onAddDeviceClick(ambiente)
            }

            devicesList.visibility = if (isExpanded) View.VISIBLE else View.GONE
        }
    }
}
