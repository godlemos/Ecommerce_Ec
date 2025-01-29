package com.example.tiendavirtualapp_kotlin2.Cliente

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tiendavirtualapp_kotlin2.Constantes
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.Vendedor.MainActivityVendedor
import com.example.tiendavirtualapp_kotlin2.databinding.ActivityRegistroClienteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.zip.Inflater

class RegistroClienteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroClienteBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnRegistrarC.setOnClickListener {
            validarInformacion()
        }

    }

    private var nombres = ""
    private var email = ""
    private var password = ""
    private var cpassword = ""

    private fun validarInformacion() {

        nombres = binding.etNombresC.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        cpassword = binding.etCPassword.text.toString().trim()

        if (nombres.isEmpty()){
            binding.etNombresC.error = "Ingrese sus nombres"
            binding.etNombresC.requestFocus()
        } else if (email.isEmpty()){
            binding.etEmail.error = "Ingrese su Email"
            binding.etEmail.requestFocus()

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.error = "Email no valido"
            binding.etEmail.requestFocus()

        } else if (password.isEmpty()){
            binding.etPassword.error = "Ingrese su password"
            binding.etPassword.requestFocus()

        } else if (password.length <=6){
            binding.etPassword.error = "Necesita 6 o màs caracteres"
            binding.etPassword.requestFocus()

        } else if (cpassword.isEmpty()){
            binding.etCPassword.error = "Confirme password"
            binding.etCPassword.requestFocus()

        } else if (password!=cpassword){
            binding.etCPassword.error = "No coinciden"
            binding.etCPassword.requestFocus()

        } else{
            registrarCliente()
        }
    }

    private fun registrarCliente() {
        progressDialog.setMessage("Creando cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                insertarInfoBD()
            }

            .addOnFailureListener { e->
                Toast.makeText(this, "Fallo el registro debido a ${e.message}",Toast.LENGTH_SHORT).show()

            }



    }

    private fun insertarInfoBD() {
        progressDialog.setMessage("Guardando informaciòn...")

        val uid = firebaseAuth.uid
        val nombresC = nombres
        val emailC = email
        val tiempoRegistro = Constantes().obtenerTiempoD()

        val datosCliente = HashMap<String, Any>()


        datosCliente["uid"] = "$uid"
        datosCliente["nombres"] = "$nombresC"
        datosCliente["email"] = "$emailC"
        datosCliente["tRegistro"] = "$tiempoRegistro"
        datosCliente["imagen"] = ""
        datosCliente["tipoUsuario"] = "cliente"

        val references = FirebaseDatabase.getInstance().getReference("Usuarios")
        references.child(uid!!)
            .setValue(datosCliente)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this@RegistroClienteActivity, MainActivityCliente::class.java))
                finishAffinity()

            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this, "Fallò el registro en BD debiado a ${e.message}",Toast.LENGTH_SHORT).show()

            }

    }
}