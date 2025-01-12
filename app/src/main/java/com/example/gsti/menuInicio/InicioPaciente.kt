package com.example.gsti.menuInicio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gsti.EstadisticasActivity
import com.example.gsti.InformacionPersonal.InformacionPacienteActivity
import com.example.gsti.InformacionPersonalActivity
import com.example.gsti.R
import com.example.gsti.SobreNosotros
import com.example.gsti.juegosPaciente.JuegosPacienteActivity

class InicioPaciente : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio_paciente)

        // Referencias a los botones usando sus IDs
        val btnJuegos = findViewById<LinearLayout>(R.id.btnJuegos)
        val btnEstadisticas = findViewById<LinearLayout>(R.id.btnEstadisticas)
        val btnNotificaciones = findViewById<LinearLayout>(R.id.btnNotificaciones)

        // Acción para "Juegos"
        btnJuegos.setOnClickListener {
            Toast.makeText(this, "Ir a Juegos", Toast.LENGTH_SHORT).show()
            // Navegar a la pantalla de orientación antes del juego
            val intent = Intent(this, JuegosPacienteActivity::class.java)
            startActivity(intent)
        }


        // Acción para "Estadísticas"
        btnEstadisticas.setOnClickListener {
            Toast.makeText(this, "Ir a Estadísticas", Toast.LENGTH_SHORT).show()
            // Navegar a EstadisticasActivity
            val intent = Intent(this, EstadisticasActivity::class.java)
            startActivity(intent)
        }


        // Verificar el rol del usuario
        val userRole = getUserRole()
        if (userRole != "paciente") {
            // Si el rol no es "paciente", mostrar un mensaje y redirigir
            Toast.makeText(this, "Acceso denegado. Usuario no autorizado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configurar el Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        }

    // Obtener el rol del usuario desde SharedPreferences
    private fun getUserRole(): String? {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("USER_ROLE", null)
    }

    // Inflar el menú en el Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu) // Asegúrate de que `menu_toolbar` existe
        return true
    }

    // Acciones al seleccionar un ítem del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_principal -> {
                Toast.makeText(this, "Ya estás en la pantalla principal", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_informacion_personal -> {
                // Redirigir a la actividad de información personal
                val intent = Intent(this, InformacionPacienteActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_sobre_nosotros -> {
                // Redirigir a la actividad "Sobre Nosotros"
                val intent = Intent(this, SobreNosotros::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
