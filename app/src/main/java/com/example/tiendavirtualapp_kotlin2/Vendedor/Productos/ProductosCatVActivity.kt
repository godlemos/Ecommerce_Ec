package com.example.tiendavirtualapp_kotlin2.Vendedor.Productos

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tiendavirtualapp_kotlin2.Adaptadores.AdaptadorProductoV
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloProducto
import com.example.tiendavirtualapp_kotlin2.databinding.ActivityProductosCatVactivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductosCatVActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductosCatVactivityBinding
    private var nombreCat = ""
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var productosList: ArrayList<ModeloProducto>
    private lateinit var adaptador: AdaptadorProductoV

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductosCatVactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Obtener datos de intent
        val intent = intent
        nombreCat = intent.getStringExtra("nombreCat") ?: ""

        // Configurar toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = nombreCat
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Inicializar lista y adaptador
        productosList = ArrayList()
        adaptador = AdaptadorProductoV(this, productosList)

        // Configurar RecyclerView
        binding.rvProductos.layoutManager = GridLayoutManager(this, 2)
        binding.rvProductos.adapter = adaptador
        
        // Cargar productos de la categoría
        cargarProductos()
        
        // Configurar búsqueda
        binding.etBuscarProducto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No se necesita implementación
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Buscar productos cuando cambia el texto
                buscarProductos(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // No se necesita implementación
            }
        })
        
        // Configurar botón de búsqueda
        binding.ibBuscar.setOnClickListener {
            val query = binding.etBuscarProducto.text.toString().trim()
            buscarProductos(query)
        }
    }
    
    private fun cargarProductos() {
        // Mostrar progreso
        binding.progressBar?.visibility = View.VISIBLE
        
        // Referencia a la base de datos
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        
        // Consultar productos por categoría
        ref.orderByChild("categoria").equalTo(nombreCat)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Limpiar lista
                    productosList.clear()
                    
                    // Recorrer resultados
                    for (ds in snapshot.children) {
                        val producto = ds.getValue(ModeloProducto::class.java)
                        producto?.let {
                            // Solo agregar productos del vendedor actual
                            if (it.uid == firebaseAuth.uid) {
                                productosList.add(it)
                            }
                        }
                    }
                    
                    // Notificar al adaptador
                    adaptador.notifyDataSetChanged()
                    
                    // Ocultar progreso
                    binding.progressBar?.visibility = View.GONE
                    
                    // Mostrar mensaje si no hay productos
                    if (productosList.isEmpty()) {
                        binding.tvNoProductos?.visibility = View.VISIBLE
                    } else {
                        binding.tvNoProductos?.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar error
                    Toast.makeText(this@ProductosCatVActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    binding.progressBar?.visibility = View.GONE
                }
            })
    }
    
    private fun buscarProductos(query: String) {
        if (query.isEmpty()) {
            // Si la consulta está vacía, mostrar todos los productos
            adaptador.updateList(productosList)
            return
        }
        
        // Filtrar productos por nombre
        val resultados = productosList.filter { 
            it.nombre.lowercase().contains(query.lowercase()) 
        }
        
        // Actualizar adaptador con resultados
        adaptador.updateList(resultados)
        
        // Mostrar mensaje si no hay resultados
        if (resultados.isEmpty()) {
            binding.tvNoProductos?.visibility = View.VISIBLE
            binding.tvNoProductos?.text = "No se encontraron productos para: $query"
        } else {
            binding.tvNoProductos?.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}