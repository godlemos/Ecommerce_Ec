package com.example.tiendavirtualapp_kotlin2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Precargar la animación Lottie
        val lottieAnimationView = findViewById<LottieAnimationView>(R.id.lottieAnimationView)
        lottieAnimationView.setAnimation("splash_tienda.json") // Asegúrate de que el nombre del archivo sea correcto
        lottieAnimationView.playAnimation()

        // Usar un Handler para redirigir después de un tiempo
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SeleccionarTipoActivity::class.java)
            startActivity(intent)
            finish()
        }, 9000) // 2000 milisegundos = 2 segundos
    }
}