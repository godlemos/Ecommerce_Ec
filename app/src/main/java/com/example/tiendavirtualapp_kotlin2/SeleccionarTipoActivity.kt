package com.example.tiendavirtualapp_kotlin2
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tiendavirtualapp_kotlin2.Cliente.LoginClienteActivity
import com.example.tiendavirtualapp_kotlin2.Vendedor.LoginVendedorActivity
import com.example.tiendavirtualapp_kotlin2.databinding.ActivitySeleccionarTipoBinding


class SeleccionarTipoActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySeleccionarTipoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeleccionarTipoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tipoVendedor.setOnClickListener {
            startActivity(Intent(this@SeleccionarTipoActivity, LoginVendedorActivity::class.java ))
        }
        binding.tipoCliente.setOnClickListener {
            startActivity(Intent(this@SeleccionarTipoActivity, LoginClienteActivity::class.java))
        }
    }
}