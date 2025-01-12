package com.example.gsti

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gsti.menuInicio.InicioFamiliar
import com.example.gsti.menuInicio.InicioMedico
import com.example.gsti.menuInicio.InicioPaciente

class SobreNosotros : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sobre_nosotros) // Referencia al layout

        // Configurar el Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        }

    // Inflar el menú en el Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)  // Inflar el menú desde el archivo XML
        return true
    }

    // Acciones cuando se selecciona un ítem del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_principal -> {
                // Redirigir al menú principal según el rol almacenado en SharedPreferences
                redirigirAlMenuPrincipal()
                true
            }
            R.id.menu_informacion_personal -> {
                // Redirigir a la actividad de información personal
                val intent = Intent(this, InformacionPersonalActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_sobre_nosotros -> {
                // Redirigir a la actividad "Sobre Nosotros" (actualmente en la misma pantalla)
                Toast.makeText(this, "Ya estás en la pantalla Sobre Nosotros", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Función para redirigir al menú principal según el rol del usuario
    private fun redirigirAlMenuPrincipal() {
        val userRole = getUserRole()

        when (userRole) {
            "medico" -> {
                // Redirigir al menú principal del médico
                val intent = Intent(this, InicioMedico::class.java)
                startActivity(intent)
            }
            "paciente" -> {
                // Redirigir al menú principal del paciente
                val intent = Intent(this, InicioPaciente::class.java)
                startActivity(intent)
            }
            "familiar" -> {
                // Redirigir al menú principal del familiar
                val intent = Intent(this, InicioFamiliar::class.java)
                startActivity(intent)
            }
            else -> {
                // Mostrar mensaje de error si no se puede determinar el rol
                Toast.makeText(this, "No se pudo determinar el tipo de usuario.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Obtener el rol del usuario desde SharedPreferences
    private fun getUserRole(): String? {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("USER_ROLE", null)
    }
}
