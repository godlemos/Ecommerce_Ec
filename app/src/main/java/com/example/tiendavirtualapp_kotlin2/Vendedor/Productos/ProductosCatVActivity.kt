package com.example.tiendavirtualapp_kotlin2.Vendedor.Productos

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloProducto
import com.example.tiendavirtualapp_kotlin2.Adaptadores.AdaptadorProducto
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.databinding.ActivityProductosCatVactivityBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductosCatVActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProductosCatVactivityBinding
    private var nombreCat = ""

    private lateinit var productoArrayList : ArrayList<ModeloProducto>
    private lateinit var adaptadorProductos : AdaptadorProducto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductosCatVactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nombreCat = intent.getStringExtra("nombreCat").toString()

        binding.txtProductoCat.text = "Categoria - ${nombreCat}"
        
        // Añadir listener para el botón de regreso usando findViewById
        val btnRegresar = findViewById<ImageButton>(R.id.Ib_regresar)
        btnRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        listarProductos(nombreCat)

    }

    private fun listarProductos(nombreCat : String) {
        productoArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.orderByChild("categoria").equalTo(nombreCat).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productoArrayList.clear()
                for (ds in snapshot.children){
                    val modeloProducto = ds.getValue(ModeloProducto::class.java)
                    productoArrayList.add(modeloProducto!!)
                }
                adaptadorProductos = AdaptadorProducto(this@ProductosCatVActivity, productoArrayList)
                binding.productosRV.adapter = adaptadorProductos
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}