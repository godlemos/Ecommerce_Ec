package com.example.tiendavirtualapp_kotlin2.Adaptadores

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendavirtualapp_kotlin2.Cliente.Orden.DetalleOrdenCActivity
import com.example.tiendavirtualapp_kotlin2.Constantes
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloOrdenCompra
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.databinding.ItemOrdenCompraVendedorBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorOrdenCompraVendedor : RecyclerView.Adapter<AdaptadorOrdenCompraVendedor.HolderOrdenCompraVendedor> {

    private lateinit var binding: ItemOrdenCompraVendedorBinding

    private var mContext: Context
    var ordenesArrayList: ArrayList<ModeloOrdenCompra>

    constructor(mContext: Context, ordenesArrayList: ArrayList<ModeloOrdenCompra>) {
        this.mContext = mContext
        this.ordenesArrayList = ordenesArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderOrdenCompraVendedor {
        binding = ItemOrdenCompraVendedorBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return HolderOrdenCompraVendedor(binding.root)
    }

    override fun getItemCount(): Int {
        return ordenesArrayList.size
    }

    override fun onBindViewHolder(holder: HolderOrdenCompraVendedor, position: Int) {
        val ordenCompra = ordenesArrayList[position]

        val idOrden = ordenCompra.idOrden
        val ordenadoPor = ordenCompra.ordenadoPor
        val tiempoOrden = ordenCompra.tiempoOrden
        val costo = ordenCompra.costo
        val estadoOrden = ordenCompra.estadoOrden

        // Mostrar información de la orden
        holder.idOrdenItem.text = "Orden ID: $idOrden"
        holder.costoOrdenItem.text = "Costo Total: $costo USD"
        holder.estadoOrdenItem.text = "Estado: $estadoOrden"

        // Obtener información del cliente
        obtenerInfoCliente(ordenadoPor, holder)

        // Establecer color según el estado
        when (estadoOrden) {
            "Solicitud recibida" -> {
                holder.estadoOrdenItem.setTextColor(ContextCompat.getColor(mContext, R.color.azul_marino_oscuro))
            }
            "Pago Pendiente" -> {
                holder.estadoOrdenItem.setTextColor(ContextCompat.getColor(mContext, R.color.morado))
            }
            "En Preparación" -> {
                holder.estadoOrdenItem.setTextColor(ContextCompat.getColor(mContext, R.color.naranja))
            }
            "Entregado" -> {
                holder.estadoOrdenItem.setTextColor(ContextCompat.getColor(mContext, R.color.verde_oscuro2))
            }
            "Cancelado" -> {
                holder.estadoOrdenItem.setTextColor(ContextCompat.getColor(mContext, R.color.rojo))
            }
        }

        // Formatear y mostrar la fecha
        try {
            val fecha = Constantes().obtenerFecha(tiempoOrden.toLong())
            holder.fechaOrdenItem.text = "Fecha: $fecha"
        } catch (e: Exception) {
            holder.fechaOrdenItem.text = "Fecha: No disponible"
        }

        // Botón para cambiar estado
        holder.btnCambiarEstado.setOnClickListener {
            mostrarDialogoCambiarEstado(ordenCompra)
        }

        // Botón para ver detalles
        holder.ibSiguiente.setOnClickListener {
            val intent = Intent(mContext, com.example.tiendavirtualapp_kotlin2.Vendedor.Orden.DetalleOrdenVActivity::class.java)
            intent.putExtra("idOrden", idOrden)
            mContext.startActivity(intent)
        }
    }

    private fun obtenerInfoCliente(uid: String, holder: HolderOrdenCompraVendedor) {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val nombre = "${snapshot.child("nombres").value}"
                        val telefono = "${snapshot.child("telefono").value}"
                        holder.clienteOrdenItem.text = "Cliente: $nombre | Tel: $telefono"
                    } else {
                        holder.clienteOrdenItem.text = "Cliente: Información no disponible"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    holder.clienteOrdenItem.text = "Cliente: Error al cargar información"
                }
            })
    }

    private fun mostrarDialogoCambiarEstado(ordenCompra: ModeloOrdenCompra) {
        val opciones = arrayOf(
            "Solicitud recibida",
            "Pago Pendiente",
            "En Preparación",
            "Entregado",
            "Cancelado"
        )

        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("Cambiar Estado de la Orden")
            .setItems(opciones) { _, which ->
                val nuevoEstado = opciones[which]
                actualizarEstadoOrden(ordenCompra, nuevoEstado)
            }
            .show()
    }

    private fun actualizarEstadoOrden(ordenCompra: ModeloOrdenCompra, nuevoEstado: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Ordenes")
        val actualizacion = HashMap<String, Any>()
        actualizacion["estadoOrden"] = nuevoEstado

        ref.child(ordenCompra.idOrden)
            .updateChildren(actualizacion)
            .addOnSuccessListener {
                Toast.makeText(mContext, "Estado actualizado a: $nuevoEstado", Toast.LENGTH_SHORT).show()
                // Actualizar el estado en el objeto local
                ordenCompra.estadoOrden = nuevoEstado
                notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(mContext, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    inner class HolderOrdenCompraVendedor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var idOrdenItem = binding.idOrdenItem
        var fechaOrdenItem = binding.fechaOrdenItem
        var clienteOrdenItem = binding.clienteOrdenItem
        var estadoOrdenItem = binding.estadoOrdenItem
        var costoOrdenItem = binding.costoOrdenItem
        var btnCambiarEstado = binding.btnCambiarEstado
        var ibSiguiente = binding.ibSiguiente
    }
} 