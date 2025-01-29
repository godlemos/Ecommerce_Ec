package com.example.tiendavirtualapp_kotlin2.Vendedor

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tiendavirtualapp_kotlin2.databinding.ActivityLoginVendedorBinding
import com.google.firebase.auth.FirebaseAuth

class LoginVendedorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginVendedorBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnLoginV.setOnClickListener {

            validarInfo()


        }

        binding.tvRegistrarV.setOnClickListener {
            startActivity(Intent(applicationContext, RegistroVendedorActivity::class.java))

        }

    }

    private var email = ""
    private var password = ""
    private fun validarInfo() {
        email = binding.etEmail.text.toString().trim()
        password= binding.etPassword.text.toString().trim()

        if (email.isEmpty()){
            binding.etEmail.error = "Ingrese email"
            binding.etEmail.requestFocus()
         }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.error = "Email no valido"
            binding.etEmail.requestFocus()

        }else if (password.isEmpty()){
            binding.etPassword.error = "Ingrese password"
            binding.etPassword.requestFocus()
        }else{
            loginVendedor()
        }

    }

    private fun loginVendedor() {
        progressDialog.setMessage("Ingresando")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivityVendedor::class.java))
                finishAffinity()
                Toast.makeText(
                    this,
                    "Bienvenido(a)",
                    Toast.LENGTH_SHORT
                ).show()

            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "No se pudo iniciar sesion debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()

            }







    }
}