package com.example.tiendavirtualapp_kotlin2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tiendavirtualapp_kotlin2.Cliente.MainActivityCliente
import com.example.tiendavirtualapp_kotlin2.Vendedor.MainActivityVendedor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        firebaseAuth = FirebaseAuth.getInstance()

        // Realiza la transición después de un retraso
        Thread {
            try {
                Thread.sleep(3000) // 3 segundos
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            // Llama a comprobarTipoUsuario después del retraso
            runOnUiThread {
                comprobarTipoUsuario()
            }
        }.start()
    }

    private fun comprobarTipoUsuario() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            // Redirige a MainActivityVendedor si no hay un usuario autenticado
            startActivity(Intent(this, SeleccionarTipoActivity::class.java))
            finish()
        } else {
            val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
            reference.child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val tipoU = snapshot.child("tipoUsuario").value
                        if (tipoU == "vendedor") {
                            startActivity(Intent(this@SplashActivity, MainActivityVendedor::class.java))
                            finishAffinity()
                        } else if (tipoU == "cliente") {
                            startActivity(Intent(this@SplashActivity, MainActivityCliente::class.java))
                            finishAffinity()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Manejo de errores (opcional)
                        error.toException().printStackTrace()
                    }
                })
        }
    }
}
