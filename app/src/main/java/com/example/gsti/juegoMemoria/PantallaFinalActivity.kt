package com.example.gsti.juegoMemoria

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.R
import com.example.gsti.juegosPaciente.JuegosPacienteActivity

class PantallaFinalActivity : AppCompatActivity() {

    private lateinit var scoreText: TextView
    private lateinit var round1ScoreText: TextView
    private lateinit var round2ScoreText: TextView
    private lateinit var round3ScoreText: TextView
    private lateinit var totalScoreText: TextView
    private lateinit var restartButton: Button
    private lateinit var menuButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final_score) // Asegúrate de usar el nombre correcto de tu archivo XML

        // Vincular las vistas
        scoreText = findViewById(R.id.scoreText)
        round1ScoreText = findViewById(R.id.round1ScoreText)
        round2ScoreText = findViewById(R.id.round2ScoreText)
        round3ScoreText = findViewById(R.id.round3ScoreText)
        totalScoreText = findViewById(R.id.totalScoreText)
        restartButton = findViewById(R.id.restartButton)
        menuButton = findViewById(R.id.menuButton)

        // Obtener las puntuaciones desde el Intent
        val round1Score = intent.getIntExtra("ROUND_1_SCORE", 0)
        val round2Score = intent.getIntExtra("ROUND_2_SCORE", 0)
        val round3Score = intent.getIntExtra("ROUND_3_SCORE", 0)
        val totalScore = intent.getIntExtra("TOTAL_SCORE", 0)

        // Obtener los colores desde los recursos
        val correctColor = resources.getColor(R.color.correct2, null)
        val incorrectColor = resources.getColor(R.color.incorrect, null)

        // Mostrar las puntuaciones
        scoreText.text = "¡Juego Completado!"
        round1ScoreText.text = "Puntuación Ronda 1: $round1Score/5"
        round2ScoreText.text = "Puntuación Ronda 2: $round2Score/5"
        round3ScoreText.text = "Puntuación Ronda 3: $round3Score/5"
        totalScoreText.text = "Puntuación Total: $totalScore/15"

        // Cambiar el color de las puntuaciones
        // Establecer el color del texto dependiendo de los puntos totales
        if (totalScore < 3) {
            totalScoreText.setTextColor(incorrectColor)  // Si el puntaje es menor a 3, color incorrecto (naranja)
        } else {
            totalScoreText.setTextColor(correctColor)  // Si el puntaje es mayor o igual a 3, color correcto (verde)
        }

        // Configurar visibilidad de botones y acciones
        if (totalScore < 7) {
            // Mostrar botón de reinicio si la puntuación es menor a 7
            restartButton.visibility = View.VISIBLE
            menuButton.visibility = View.GONE

            restartButton.setOnClickListener {
                val intent = Intent(this, MecanicaMemoria::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            // Mostrar botón de menú si la puntuación es mayor o igual a 7
            restartButton.visibility = View.GONE
            menuButton.visibility = View.VISIBLE

            menuButton.setOnClickListener {
                // Navegar al menú cuando el usuario presione el botón
                val intent = Intent(this, JuegosPacienteActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
