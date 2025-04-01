package com.example.tiendavirtualapp_kotlin2.Vendedor.Nav_Fragments_Vendedor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.tiendavirtualapp_kotlin2.Vendedor.ListaClientes.ListaClientesActivity
import com.example.tiendavirtualapp_kotlin2.databinding.FragmentMiTiendaVBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentMiTiendaV : Fragment() {

    private lateinit var binding: FragmentMiTiendaVBinding
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMiTiendaVBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Cargar datos
        cargarEstadosOrden()
        cargarGananciasYPerdidas()
        
        // Configurar listeners
        configurarListeners()
    }
    
    private fun configurarListeners() {
        // Botón para ver clientes
        binding.btnVerCliente.setOnClickListener {
            startActivity(Intent(mContext, ListaClientesActivity::class.java))
        }
    }

    private fun cargarGananciasYPerdidas() {
        val ref = FirebaseDatabase.getInstance().getReference("Ordenes")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var total = 0.0
                var totalPerdida = 0.0

                for (ordenSn in snapshot.children) {
                    val estadoOrden = ordenSn.child("estadoOrden").value
                    val costo = ordenSn.child("costo").getValue(String::class.java)

                    if (estadoOrden == "Entregado" && costo != null) {
                        val costoValue = costo.replace("[^\\d.]".toRegex(), "").toDoubleOrNull()
                        if (costoValue != null) {
                            total += costoValue
                        }
                    } else if (estadoOrden == "Cancelado" && costo != null) {
                        val costoValue = costo.replace("[^\\d.]".toRegex(), "").toDoubleOrNull()
                        if (costoValue != null) {
                            totalPerdida += costoValue
                        }
                    }
                }

                binding.tvGanancias.text = String.format("%.2f USD", total)
                binding.tvPerdidas.text = String.format("%.2f USD", totalPerdida)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(mContext, "Error al cargar datos financieros: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarEstadosOrden() {
        val ref = FirebaseDatabase.getInstance().getReference("Ordenes")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var contador_1 = 0
                var contador_2 = 0
                var contador_3 = 0
                var contador_4 = 0
                var contador_5 = 0

                for (ordenSn in snapshot.children) {
                    val estadoOrden = ordenSn.child("estadoOrden").value

                    when (estadoOrden) {
                        "Solicitud recibida" -> {
                            contador_1++
                            binding.tvEstado1.text = "Solicitudes recibidas: $contador_1"
                        }
                        "Pago Pendiente" -> {
                            contador_2++
                            binding.tvEstado2.text = "Pagos pendientes: $contador_2"
                        }
                        "En Preparación" -> {
                            contador_3++
                            binding.tvEstado3.text = "Órdenes en preparación: $contador_3"
                        }
                        "Entregado" -> {
                            contador_4++
                            binding.tvEstado4.text = "Órdenes entregadas: $contador_4"
                        }
                        "Cancelado" -> {
                            contador_5++
                            binding.tvEstado5.text = "Órdenes canceladas: $contador_5"
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(mContext, "Error al cargar estados de órdenes: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}