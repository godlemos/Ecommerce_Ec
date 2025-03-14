package com.example.tiendavirtualapp_kotlin2.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloProducto
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.databinding.ItemProductoBinding
import android.graphics.Paint

class AdaptadorProductoV(
    private val context: Context,
    private var productosList: ArrayList<ModeloProducto>
) : RecyclerView.Adapter<AdaptadorProductoV.HolderProductoV>() {

    private lateinit var binding: ItemProductoBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProductoV {
        binding = ItemProductoBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderProductoV(binding.root)
    }

    override fun onBindViewHolder(holder: HolderProductoV, position: Int) {
        val modelo = productosList[position]
        
        // Establecer datos
        holder.binding.itemNombreP.text = modelo.nombre
        holder.binding.itemCategoriaP.text = modelo.categoria
        
        // Verificar si hay descuento
        if (!modelo.precioDescuento.equals("0") && modelo.descuento > 0) {
            // Habilitar las vistas
            holder.binding.itemNotaP.visibility = View.VISIBLE
            holder.binding.itemPrecioPDesc.visibility = View.VISIBLE
            
            // Setear la información
            holder.binding.itemNotaP.text = "${modelo.descuento}% OFF"
            holder.binding.itemPrecioPDesc.text = "${modelo.precioDescuento} USD"
            holder.binding.itemPrecioP.text = "${modelo.precio} USD"
            holder.binding.itemPrecioP.paintFlags = holder.binding.itemPrecioP.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG // Tachando el precio original
        } else {
            // El producto no tiene descuento
            // Ocultar las vistas
            holder.binding.itemNotaP.visibility = View.GONE
            holder.binding.itemPrecioPDesc.visibility = View.GONE
            
            // Setear la información
            holder.binding.itemPrecioP.text = "${modelo.precio} USD"
            holder.binding.itemPrecioP.paintFlags = holder.binding.itemPrecioP.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv() // Quitar el tachado
        }
        
        // Cargar imagen
        try {
            Glide.with(context)
                .load(modelo.imagenUrl)
                .placeholder(R.drawable.item_img_producto)
                .into(holder.binding.imagenP)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Manejar clics
        holder.itemView.setOnClickListener {
            // Implementar navegación a detalle de producto si es necesario
        }
    }

    override fun getItemCount(): Int {
        return productosList.size
    }
    
    // Método para actualizar la lista con resultados de búsqueda
    fun updateList(newList: List<ModeloProducto>) {
        productosList = ArrayList(newList)
        notifyDataSetChanged()
    }
    
    inner class HolderProductoV(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemProductoBinding.bind(itemView)
    }
} 