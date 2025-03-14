package com.example.tiendavirtualapp_kotlin2.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloItemOrden
import com.example.tiendavirtualapp_kotlin2.R

class AdaptadorItemOrden : RecyclerView.Adapter<AdaptadorItemOrden.HolderItemOrden> {

    private val context: Context
    private val itemsOrdenList: ArrayList<ModeloItemOrden>

    constructor(context: Context, itemsOrdenList: ArrayList<ModeloItemOrden>) {
        this.context = context
        this.itemsOrdenList = itemsOrdenList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderItemOrden {
        val view = LayoutInflater.from(context).inflate(R.layout.item_producto_orden, parent, false)
        return HolderItemOrden(view)
    }

    override fun getItemCount(): Int {
        return itemsOrdenList.size
    }

    override fun onBindViewHolder(holder: HolderItemOrden, position: Int) {
        // Obtener datos
        val modeloItem = itemsOrdenList[position]
        val nombre = modeloItem.nombre
        val costo = modeloItem.costo
        val cantidad = modeloItem.cantidad
        val precioFinal = modeloItem.precioFinal

        // Establecer datos
        holder.nombreTv.text = nombre
        holder.precioTv.text = "Precio: $costo USD"
        holder.cantidadTv.text = "Cantidad: $cantidad"
        holder.precioFinalTv.text = "Subtotal: $precioFinal USD"
    }

    inner class HolderItemOrden(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Vistas del item_producto_orden.xml
        val nombreTv: TextView = itemView.findViewById(R.id.itemNombreP)
        val precioTv: TextView = itemView.findViewById(R.id.itemPrecioP)
        val cantidadTv: TextView = itemView.findViewById(R.id.itemCantidadP)
        val precioFinalTv: TextView = itemView.findViewById(R.id.itemPrecioFinalP)
    }
} 