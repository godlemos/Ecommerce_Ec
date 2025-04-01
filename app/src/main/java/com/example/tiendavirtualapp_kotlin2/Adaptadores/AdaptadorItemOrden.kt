package com.example.tiendavirtualapp_kotlin2.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloItemOrden
import com.example.tiendavirtualapp_kotlin2.databinding.ItemProductoOrdenBinding

class AdaptadorItemOrden : RecyclerView.Adapter<AdaptadorItemOrden.HolderItemOrden> {

    private lateinit var binding: ItemProductoOrdenBinding
    private val context: Context
    private val itemsOrdenList: ArrayList<ModeloItemOrden>

    constructor(context: Context, itemsOrdenList: ArrayList<ModeloItemOrden>) {
        this.context = context
        this.itemsOrdenList = itemsOrdenList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderItemOrden {
        binding = ItemProductoOrdenBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderItemOrden(binding.root)
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
        holder.precioTv.text = "Precio unidad: $costo USD"
        holder.cantidadTv.text = "Cantidad: $cantidad"
        holder.precioFinalTv.text = "Precio suma total: $precioFinal USD"
    }

    inner class HolderItemOrden(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Vistas del item_producto_orden.xml usando ViewBinding
        val nombreTv = binding.itemNombreP
        val precioTv = binding.itemPrecioP
        val cantidadTv = binding.itemCantidadP
        val precioFinalTv = binding.itemPrecioFinalP
    }
} 