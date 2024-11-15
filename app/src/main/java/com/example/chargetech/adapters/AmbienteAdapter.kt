package com.example.chargetech.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chargetech.R
import com.example.chargetech.activities.NewEnvironmentActivity
import com.example.chargetech.activities.ProfileActivity
import com.example.chargetech.models.Ambiente
import com.example.chargetech.repositories.AmbienteRepository

class AmbienteAdapter(
    private val ambientes: MutableList<Ambiente>,
    private val onAddDeviceClick: (Ambiente) -> Unit
) : RecyclerView.Adapter<AmbienteAdapter.AmbienteViewHolder>() {

    private val ambienteRepository = AmbienteRepository()
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
        private val deleteEnvironmentButton: Button = itemView.findViewById(R.id.deleteEnvironmentButton)
        private val editEnvironmentButton: Button = itemView.findViewById(R.id.editEnvironmentButton)
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

            editEnvironmentButton.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, NewEnvironmentActivity::class.java)
                intent.putExtra("id_ambiente", ambiente.id_ambiente)
                context.startActivity(intent)
            }

            deleteEnvironmentButton.setOnClickListener {
                val context = itemView.context
                val alertDialog = android.app.AlertDialog.Builder(context)
                    .setTitle("Excluir ambiente")
                    .setMessage("Você tem certeza que deseja excluir este ambiente?")
                    .setPositiveButton("Sim") { dialog, _ ->
                        val position = bindingAdapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            ambienteRepository.delete(ambiente.id_ambiente) { success, errorMessage ->
                                if (success) {
                                    ambientes.removeAt(position)
                                    val profileIntent = Intent(context, ProfileActivity::class.java)
                                    context.startActivity(profileIntent)
                                } else {
                                    android.widget.Toast.makeText(context, errorMessage ?: "Erro desconhecido", android.widget.Toast.LENGTH_SHORT).show()
                                }
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

            devicesList.visibility = if (isExpanded) View.VISIBLE else View.GONE
        }
    }
}
