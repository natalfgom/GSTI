package com.example.gsti.Orientaciones

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.R

class OrientacionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.orientacion_activity1)

        // Obtén los elementos de la vista
        val titleTextView = findViewById<TextView>(R.id.titleTextView)
        val descriptionTextView = findViewById<TextView>(R.id.descriptionTextView)
        val nextButton = findViewById<Button>(R.id.nextButton)

        // Recibe el texto dinámico desde el intent
        val gameDescription = intent.getStringExtra("gameDescription") ?: getString(R.string.description_game_1)

        // Configura los textos
        titleTextView.text = getString(R.string.title_orientation)
        descriptionTextView.text = gameDescription

        nextButton.setOnClickListener {
            val intent = Intent(this, ComprensionActivity::class.java)
            startActivity(intent)
        }

    }
}