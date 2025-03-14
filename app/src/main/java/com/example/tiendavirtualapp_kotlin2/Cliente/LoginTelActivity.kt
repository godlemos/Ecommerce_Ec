package com.example.tiendavirtualapp_kotlin2.Cliente

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tiendavirtualapp_kotlin2.Constantes
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.databinding.ActivityLoginTelBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class LoginTelActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginTelBinding
    private lateinit var progressDialog : ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth

    private var forceResendingToken : ForceResendingToken?=null
    private lateinit var mCallback : OnVerificationStateChangedCallbacks
    private var mVerification : String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginTelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.rlTelefono.visibility = View.VISIBLE
        binding.rlCodigoVer.visibility = View.GONE

        phoneLoginCallbacks()

        binding.btnEnviarCodigo.setOnClickListener {
            validarData()
        }

        binding.btnVerificarCod.setOnClickListener {
            val otp = binding.etCodVer.text.toString().trim()
            if (otp.isEmpty()){
                binding.etCodVer.error = "Ingrese código"
                binding.etCodVer.requestFocus()
            }else if (otp.length<6){
                binding.etCodVer.error = "El código debe contener 6 car."
                binding.etCodVer.requestFocus()
            }else{
                verificarCodTel(otp)
            }
        }

        binding.tvReenviarCod.setOnClickListener {
            if (forceResendingToken != null){
                reenviarCodVer()
            }else{
                Toast.makeText(this, "No se puede reeviar el código",
                    Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun verificarCodTel(otp: String) {
        progressDialog.setMessage("Verificando código")
        progressDialog.show()

        val credencial = PhoneAuthProvider.getCredential(mVerification!!, otp)
        signInWithPhoneAuthCredencial(credencial)

    }

    private fun signInWithPhoneAuthCredencial(credencial: PhoneAuthCredential) {
        progressDialog.setMessage("Ingresando")
        progressDialog.show()

        firebaseAuth.signInWithCredential(credencial)
            .addOnSuccessListener { authResult->
                if (authResult.additionalUserInfo!!.isNewUser){
                    guardarInfo()
                }else{
                    startActivity(Intent(this, MainActivityCliente::class.java))
                    finishAffinity()
                }
            }
    }

    private fun guardarInfo() {
        progressDialog.setMessage("Guardando información")
        progressDialog.show()

        val uid = firebaseAuth.uid
        val tiempoReg = Constantes().obtenerTiempoD()

        val datosCliente = HashMap<String, Any>()

        datosCliente["uid"] = "${uid}"
        datosCliente["nombres"] = ""
        datosCliente["telefono"] = "${codTelnumTel}"
        datosCliente["email"] = ""
        datosCliente["dni"] = ""
        datosCliente["proveedor"] = "telefono"
        datosCliente["tRegistro"] = tiempoReg
        datosCliente["imagen"] = ""
        datosCliente["tipoUsuario"] = "cliente"

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uid!!)
            .setValue(datosCliente)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this , MainActivityCliente::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(
                    this@LoginTelActivity,
                    "${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }

    private fun reenviarCodVer() {
        progressDialog.setMessage("Enviando código a ${numeroTel}")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(codTelnumTel)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallback)
            .setForceResendingToken(forceResendingToken!!)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private var codigoTel = "" //+51
    private var numeroTel = "" //123456789
    private var codTelnumTel = "" //+51 123456789
    private fun validarData() {
        codigoTel = binding.TelCodePicker.selectedCountryCodeWithPlus
        numeroTel = binding.etTelefonoC.text.toString().trim()
        codTelnumTel = codigoTel + numeroTel

        if (numeroTel.isEmpty()){
            binding.etTelefonoC.error = "Ingrese número telefónico"
            binding.etTelefonoC.requestFocus()
        }else{
            verificarNumeroTel()
        }

    }

    private fun verificarNumeroTel() {
        progressDialog.setMessage("Enviando código a ${numeroTel}")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(codTelnumTel)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallback)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun phoneLoginCallbacks() {
        mCallback = object : OnVerificationStateChangedCallbacks(){
            override fun onCodeSent(verificationId: String, token: ForceResendingToken) {
                mVerification = verificationId
                forceResendingToken = token

                progressDialog.dismiss()

                binding.rlTelefono.visibility = View.GONE
                binding.rlCodigoVer.visibility = View.VISIBLE

                Toast.makeText(this@LoginTelActivity, "Enviando código ${codTelnumTel}", Toast.LENGTH_SHORT).show()
            }
            override fun onVerificationCompleted(phoneAuthCredencial: PhoneAuthCredential) {
                signInWithPhoneAuthCredencial(phoneAuthCredencial)
            }
            override fun onVerificationFailed(e: FirebaseException) {
                progressDialog.dismiss()
                Toast.makeText(
                    this@LoginTelActivity,
                    "Falló la verificación debido a ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}