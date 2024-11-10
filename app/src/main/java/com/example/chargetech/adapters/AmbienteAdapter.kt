package com.example.chargetech.adapters

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
        private val addNewDeviceButton: Button = itemView.findViewById(R.id.addNewDeviceButton)

        fun bind(ambiente: Ambiente, isExpanded: Boolean) {
            environmentName.text = ambiente.nome
            environmentDescription.text = ambiente.descricao
            devicesList.layoutManager = LinearLayoutManager(itemView.context)
            devicesList.adapter = DispositivoAdapter(ambiente.dispositivos)

            addNewDeviceButton.setOnClickListener {
                onAddDeviceClick(ambiente)
            }

            devicesList.visibility = if (isExpanded) View.VISIBLE else View.GONE
        }
    }
}
