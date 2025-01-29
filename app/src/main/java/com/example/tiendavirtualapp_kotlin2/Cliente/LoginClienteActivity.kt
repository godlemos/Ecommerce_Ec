package com.example.tiendavirtualapp_kotlin2.Cliente

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.databinding.ActivityLoginClienteBinding
import com.example.tiendavirtualapp_kotlin2.databinding.ActivityLoginVendedorBinding

class LoginClienteActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginClienteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRegistrarC.setOnClickListener {
            startActivity(Intent(this@LoginClienteActivity, RegistroClienteActivity::class.java))

        }
    }
}