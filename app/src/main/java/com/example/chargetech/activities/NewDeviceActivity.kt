package com.example.chargetech.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.example.chargetech.R
import com.example.chargetech.repositories.DeviceRepository

class NewDeviceActivity : Activity() {

    private var deviceRepository = DeviceRepository()

    private lateinit var deviceDeviceSpinner: Spinner
    private lateinit var addNewDeviceButton: Button

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.new_device_layout)

        deviceDeviceSpinner = findViewById(R.id.deviceSpinner)
        addNewDeviceButton = findViewById(R.id.addNewDeviceButton)

        deviceRepository.getSelectOptions { ambientes, errorMessage ->
            runOnUiThread {
                if (ambientes != null && ambientes.isNotEmpty()) {
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ambientes)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    deviceDeviceSpinner.adapter = adapter
                } else {
                    addNewDeviceButton.visibility = View.VISIBLE
                    deviceDeviceSpinner.visibility = View.GONE
                }
            }
        }

        addNewDeviceButton.setOnClickListener {
            val intent = Intent(this, NewEnvironmentActivity::class.java)
            startActivity(intent)
        }
    }


}
