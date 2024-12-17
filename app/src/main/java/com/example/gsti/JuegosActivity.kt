package com.example.gsti

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView

class JuegosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juegos)

        // Referencias a los elementos del layout
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewJuegos)
        val textViewNoJuegos = findViewById<TextView>(R.id.textViewNoJuegos)

        // Lista de juegos asignados (ejemplo temporal)
        val juegosAsignados = listOf<String>() // Cambia a listOf("Juego 1", "Juego 2") para probar

        // Verificamos si la lista está vacía
        if (juegosAsignados.isEmpty()) {
            // Mostrar mensaje "No hay juegos asignados"
            textViewNoJuegos.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            // Mostrar la lista de juegos en el RecyclerView
            textViewNoJuegos.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            // Configuración del RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = JuegosAdapter(juegosAsignados)
        }
    }
}
