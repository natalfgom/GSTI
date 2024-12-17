package com.example.gsti

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JuegosAdapter(private val juegosList: List<String>) : RecyclerView.Adapter<JuegosAdapter.JuegosViewHolder>() {

    // ViewHolder que contendrá la vista de cada elemento
    class JuegosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val juegoNombreTextView: TextView = itemView.findViewById(R.id.textViewJuegoNombre)
    }

    // Inflar el layout de cada elemento
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JuegosViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_juego, parent, false)
        return JuegosViewHolder(itemView)
    }

    // Asignar datos a cada vista
    override fun onBindViewHolder(holder: JuegosViewHolder, position: Int) {
        val juego = juegosList[position] // Obtener el nombre del juego
        holder.juegoNombreTextView.text = juego
    }

    // Devolver el tamaño de la lista
    override fun getItemCount(): Int = juegosList.size
}
