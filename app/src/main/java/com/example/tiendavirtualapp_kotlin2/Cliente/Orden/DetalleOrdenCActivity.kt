package com.example.tiendavirtualapp_kotlin2.Cliente.Orden

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.tiendavirtualapp_kotlin2.Adaptadores.AdaptadorProductoOrden
import com.example.tiendavirtualapp_kotlin2.Cliente.MainActivityCliente
import com.example.tiendavirtualapp_kotlin2.Constantes
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloProductoOrden
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.databinding.ActivityDetalleOrdenCactivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson


class DetalleOrdenCActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetalleOrdenCactivityBinding
    private var idOrden = ""
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var productosArrayList : ArrayList<ModeloProductoOrden>
    private lateinit var productoOrdenAdapter : AdaptadorProductoOrden

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleOrdenCactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        idOrden = intent.getStringExtra("idOrden") ?: ""

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Configurar el botón Continuar para redirigir a Mis Órdenes
        binding.btnContinuarPago.setOnClickListener {
            // Crear un intent para volver a MainActivityCliente con el fragmento de Mis Órdenes seleccionado
            val intent = Intent(this, MainActivityCliente::class.java)
            intent.putExtra("fragmentToLoad", "misOrdenes")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        datosOrden()
        direccionCliente()
        productosOrden()
    }


    private fun productosOrden(){
        productosArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Ordenes").child(idOrden).child("Productos")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                productosArrayList.clear()
                for (ds in snapshot.children){
                    val modeloProductoOrden = ds.getValue(ModeloProductoOrden::class.java)
                    productosArrayList.add(modeloProductoOrden!!)
                }

                productoOrdenAdapter = AdaptadorProductoOrden(this@DetalleOrdenCActivity, productosArrayList)
                binding.ordenesRv.adapter = productoOrdenAdapter

                binding.cantidadOrdenD.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })





    }

    private fun direccionCliente(){
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val direccion = "${snapshot.child("direccion").value}"


                    if (direccion.isNotEmpty()){
                        //Si el usuario registró su ubicación
                        binding.direccionOrdenD.text = direccion
                    }else{
                        //Si el usuario no registró su ubicación
                        binding.direccionOrdenD.text = "Registre su ubicación para continuar"
                        binding.btnContinuarPago.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    var costo = ""
    var ordenadoPor = ""
    private fun datosOrden(){
        val ref = FirebaseDatabase.getInstance().getReference("Ordenes").child(idOrden)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val idOrden = "${snapshot.child("idOrden").value}"
                costo = "${snapshot.child("costo").value}"
                val tiempoOrden = "${snapshot.child("tiempoOrden").value}"
                val estadoOrden = "${snapshot.child("estadoOrden").value}"
                ordenadoPor = "${snapshot.child("ordenadoPor").value}"

                val fecha = Constantes().obtenerFecha(tiempoOrden.toLong())

                binding.idOrdenD.text = idOrden
                binding.fechaOrdenD.text = fecha
                binding.estadoOrdenD.text = estadoOrden
                binding.costoOrdenD.text = costo

                if (estadoOrden.equals("Solicitud recibida")){
                    binding.estadoOrdenD.setTextColor(ContextCompat.getColor(this@DetalleOrdenCActivity, R.color.azul_marino_oscuro))
                }else if (estadoOrden.equals("En preparación")){
                    binding.estadoOrdenD.setTextColor(ContextCompat.getColor(this@DetalleOrdenCActivity, R.color.naranja))
                }else if (estadoOrden.equals("Entregado")){
                    binding.estadoOrdenD.setTextColor(ContextCompat.getColor(this@DetalleOrdenCActivity, R.color.verde_oscuro2))
                }else if (estadoOrden.equals("Cancelado")){
                    binding.estadoOrdenD.setTextColor(ContextCompat.getColor(this@DetalleOrdenCActivity, R.color.rojo))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })







    }
}