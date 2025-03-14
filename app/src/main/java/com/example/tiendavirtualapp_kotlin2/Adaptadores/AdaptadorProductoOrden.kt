package com.example.tiendavirtualapp_kotlin2.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloProductoOrden
import com.example.tiendavirtualapp_kotlin2.databinding.ItemProductoOrdenBinding

class AdaptadorProductoOrden : RecyclerView.Adapter<AdaptadorProductoOrden.HolderProductoOrden> {

    private lateinit var binding : ItemProductoOrdenBinding
    private var mContext : Context
    private var productosArrayList : ArrayList<ModeloProductoOrden>

    constructor(mContext: Context, productosArrayList: ArrayList<ModeloProductoOrden>) {
        this.mContext = mContext
        this.productosArrayList = productosArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProductoOrden {
        binding = ItemProductoOrdenBinding.inflate(LayoutInflater.from(mContext), parent , false)
        return HolderProductoOrden(binding.root)
    }

    override fun getItemCount(): Int {
        return productosArrayList.size
    }

    override fun onBindViewHolder(holder: HolderProductoOrden, position: Int) {
        val modeloProductoOrden = productosArrayList[position]

        val nombre = modeloProductoOrden.nombre
        val precio = modeloProductoOrden.precio
        val cantidad = modeloProductoOrden.cantidad
        val precioFinal = modeloProductoOrden.precioFinal

        holder.itemNombreP.text = nombre
        holder.itemPrecioP.text = "Precio unidad: ${precio.plus(" USD")}"
        holder.itemCantidadP.text = "Cantidad: ${cantidad}"
        holder.itemPrecioFinalP.text = "Precio suma total: ${precioFinal.plus(" USD")}"
    }

    inner class HolderProductoOrden (itemView : View) : RecyclerView.ViewHolder(itemView){
        var itemNombreP = binding.itemNombreP
        var itemPrecioP = binding.itemPrecioP
        var itemCantidadP = binding.itemCantidadP
        var itemPrecioFinalP = binding.itemPrecioFinalP
    }



}