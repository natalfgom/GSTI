package com.example.gsti.juegoMemoria

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.R

class PantallaFinalActivity : AppCompatActivity() {

    private lateinit var scoreText: TextView
    private lateinit var round1ScoreText: TextView
    private lateinit var round2ScoreText: TextView
    private lateinit var round3ScoreText: TextView
    private lateinit var totalScoreText: TextView
    private lateinit var restartButton: Button

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

        // Obtener las puntuaciones desde el Intent
        val round1Score = intent.getIntExtra("ROUND_1_SCORE", 0)
        val round2Score = intent.getIntExtra("ROUND_2_SCORE", 0)
        val round3Score = intent.getIntExtra("ROUND_3_SCORE", 0)
        val totalScore = intent.getIntExtra("TOTAL_SCORE", 0)

        // Mostrar las puntuaciones
        scoreText.text = "¡Juego Completado!"
        round1ScoreText.text = "Puntuación Ronda 1: $round1Score/5"
        round2ScoreText.text = "Puntuación Ronda 2: $round2Score/5"
        round3ScoreText.text = "Puntuación Ronda 3: $round3Score/5"
        totalScoreText.text = "Puntuación Total: $totalScore/15"

        // Configurar el botón de reinicio
        restartButton.setOnClickListener {
            val intent = Intent(this, MecanicaMemoria::class.java)
            startActivity(intent)
            finish() // Finaliza esta actividad para regresar al juego
        }
    }
}
