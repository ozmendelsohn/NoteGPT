package com.cam.notegpt

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.cam.notegpt.Settings

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val editTextAPIKey = findViewById<EditText>(R.id.editTextAPIKey)
        val buttonSave = findViewById<Button>(R.id.buttonSave)

        // Load the saved API key if it exists
        editTextAPIKey.setText(Settings.OPENAI_API_KEY)

        buttonSave.setOnClickListener {
            // Save the entered API key when the save button is clicked
            val apiKey = editTextAPIKey.text.toString()
            Settings.OPENAI_API_KEY = apiKey

            // End the activity after saving the API key
            finish()
        }
    }
}
