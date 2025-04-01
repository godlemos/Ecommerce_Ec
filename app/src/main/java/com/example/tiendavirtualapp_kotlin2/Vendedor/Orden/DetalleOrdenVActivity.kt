package com.example.tiendavirtualapp_kotlin2.Vendedor.Orden

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tiendavirtualapp_kotlin2.Adaptadores.AdaptadorItemOrden
import com.example.tiendavirtualapp_kotlin2.Constantes
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloItemOrden
import com.example.tiendavirtualapp_kotlin2.databinding.ActivityDetalleOrdenVBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetalleOrdenVActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleOrdenVBinding
    private var idOrden = ""
    private lateinit var itemsOrdenArrayList: ArrayList<ModeloItemOrden>
    private lateinit var adaptadorItemOrden: AdaptadorItemOrden

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleOrdenVBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el ID de la orden del intent
        idOrden = intent.getStringExtra("idOrden") ?: ""

        // Inicializar lista y adaptador
        itemsOrdenArrayList = ArrayList()
        adaptadorItemOrden = AdaptadorItemOrden(this, itemsOrdenArrayList)
        binding.rvItemsOrden.adapter = adaptadorItemOrden

        // Cargar detalles de la orden
        cargarDetallesOrden()

        // Configurar botones
        binding.btnCambiarEstado.setOnClickListener {
            mostrarDialogoCambiarEstado()
        }

        binding.btnLlamar.setOnClickListener {
            val telefono = binding.tvTelC.text.toString().trim()
            if (telefono.isNotEmpty() && telefono != "No disponible") {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$telefono")
                startActivity(intent)
            } else {
                Toast.makeText(this, "Número de teléfono no disponible", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSms.setOnClickListener {
            val telefono = binding.tvTelC.text.toString().trim()
            if (telefono.isNotEmpty() && telefono != "No disponible") {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("smsto:$telefono")
                intent.putExtra("sms_body", "Hola, sobre tu orden #$idOrden...")
                startActivity(intent)
            } else {
                Toast.makeText(this, "Número de teléfono no disponible", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ibRegresar.setOnClickListener {
            onBackPressed()
        }
    }

    private fun cargarDetallesOrden() {
        // Referencia a la orden
        val refOrden = FirebaseDatabase.getInstance().getReference("Ordenes").child(idOrden)

        refOrden.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Obtener información básica de la orden
                val idOrden = "${snapshot.child("idOrden").value}"
                val costo = "${snapshot.child("costo").value}"
                val estadoOrden = "${snapshot.child("estadoOrden").value}"
                val ordenadoPor = "${snapshot.child("ordenadoPor").value}"
                val direccion = "${snapshot.child("direccion").value}"
                val tiempoOrden = "${snapshot.child("tiempoOrden").value}"

                // Mostrar información de la orden
                binding.idOrdenD.text = idOrden
                binding.costoOrdenD.text = "$costo USD"
                binding.estadoOrdenD.text = estadoOrden
                binding.direccionOrdenD.text = direccion

                // Formatear y mostrar la fecha
                try {
                    val fecha = Constantes().obtenerFecha(tiempoOrden.toLong())
                    binding.fechaOrdenD.text = fecha
                } catch (e: Exception) {
                    binding.fechaOrdenD.text = "No disponible"
                }

                // Cargar información del cliente
                cargarInfoCliente(ordenadoPor)

                // Cargar items de la orden
                cargarItemsOrden()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetalleOrdenVActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarInfoCliente(uid: String) {
        val refUsuario = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid)

        refUsuario.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nombres = "${snapshot.child("nombres").value}"
                val dni = "${snapshot.child("dni").value}"
                val telefono = "${snapshot.child("telefono").value}"

                // Mostrar información del cliente
                binding.tvNombresC.text = nombres
                binding.tvDniC.text = if (dni == "null") "No disponible" else dni
                binding.tvTelC.text = if (telefono == "null") "No disponible" else telefono
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetalleOrdenVActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarItemsOrden() {
        // Limpiar lista
        itemsOrdenArrayList.clear()

        // Referencia a los productos de la orden (cambiado de "Items" a "Productos" como en el cliente)
        val refProductos = FirebaseDatabase.getInstance().getReference("Ordenes").child(idOrden).child("Productos")
        
        // Log para verificar la ruta de la referencia
        println("DEBUG: Cargando productos de la orden desde: ${refProductos.toString()}")

        refProductos.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Limpiar lista
                itemsOrdenArrayList.clear()
                
                // Log para verificar si hay datos
                println("DEBUG: Snapshot tiene datos: ${snapshot.exists()}, Cantidad de hijos: ${snapshot.childrenCount}")

                // Recorrer productos
                for (ds in snapshot.children) {
                    try {
                        // Log para verificar cada producto
                        println("DEBUG: Producto encontrado: ${ds.key}")
                        
                        val modeloItem = ModeloItemOrden()
                        modeloItem.idProducto = "${ds.child("idProducto").value}"
                        modeloItem.nombre = "${ds.child("nombre").value}"
                        // Verificar si existe el campo precio o costo
                        if (ds.hasChild("precio")) {
                            modeloItem.costo = "${ds.child("precio").value}"
                        } else if (ds.hasChild("costo")) {
                            modeloItem.costo = "${ds.child("costo").value}"
                        }
                        modeloItem.cantidad = "${ds.child("cantidad").value}"
                        modeloItem.precioFinal = "${ds.child("precioFinal").value}"
                        
                        // Log para verificar los datos del producto
                        println("DEBUG: Producto cargado - Nombre: ${modeloItem.nombre}, Precio: ${modeloItem.costo}, Cantidad: ${modeloItem.cantidad}")

                        // Agregar a la lista
                        itemsOrdenArrayList.add(modeloItem)
                    } catch (e: Exception) {
                        println("DEBUG: Error al cargar producto: ${e.message}")
                        Toast.makeText(this@DetalleOrdenVActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                // Actualizar adaptador
                adaptadorItemOrden.notifyDataSetChanged()
                
                // Log para verificar la cantidad de productos cargados
                println("DEBUG: Total de productos cargados: ${itemsOrdenArrayList.size}")

                // Mostrar cantidad de productos
                binding.cantidadOrdenD.text = "${itemsOrdenArrayList.size}"
            }

            override fun onCancelled(error: DatabaseError) {
                println("DEBUG: Error en la carga de productos: ${error.message}")
                Toast.makeText(this@DetalleOrdenVActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarDialogoCambiarEstado() {
        val opciones = arrayOf(
            "Solicitud recibida",
            "Pago Pendiente",
            "En Preparación",
            "Entregado",
            "Cancelado"
        )

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cambiar Estado de la Orden")
            .setItems(opciones) { _, which ->
                val nuevoEstado = opciones[which]
                actualizarEstadoOrden(nuevoEstado)
            }
            .show()
    }

    private fun actualizarEstadoOrden(nuevoEstado: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Ordenes")
        val actualizacion = HashMap<String, Any>()
        actualizacion["estadoOrden"] = nuevoEstado

        ref.child(idOrden)
            .updateChildren(actualizacion)
            .addOnSuccessListener {
                Toast.makeText(this, "Estado actualizado a: $nuevoEstado", Toast.LENGTH_SHORT).show()
                binding.estadoOrdenD.text = nuevoEstado
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
} 