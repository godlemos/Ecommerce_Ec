package com.example.tiendavirtualapp_kotlin2.Adaptadores

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendavirtualapp_kotlin2.Cliente.Orden.DetalleOrdenCActivity
import com.example.tiendavirtualapp_kotlin2.Constantes
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloOrdenCompra
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.databinding.ItemOrdenCompraBinding

class AdaptadorOrdenCompra : RecyclerView.Adapter<AdaptadorOrdenCompra.HolderOrdenCompra> {

    private lateinit var binding : ItemOrdenCompraBinding

    private var mContext : Context
    var ordenesArrayList : ArrayList<ModeloOrdenCompra>

    constructor(mContext: Context, ordenesArrayList: ArrayList<ModeloOrdenCompra>) {
        this.mContext = mContext
        this.ordenesArrayList = ordenesArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderOrdenCompra {
        binding = ItemOrdenCompraBinding.inflate(LayoutInflater.from(mContext),parent,false)
        return HolderOrdenCompra(binding.root)
    }

    override fun getItemCount(): Int {
        return ordenesArrayList.size
    }

    override fun onBindViewHolder(holder: HolderOrdenCompra, position: Int) {
        val ordenCompra = ordenesArrayList[position]

        val idOrden = ordenCompra.idOrden
        val tiempoOrden = ordenCompra.tiempoOrden
        val costo = ordenCompra.costo
        val estadoOrden = ordenCompra.estadoOrden

        holder.idOrdenItem.text = idOrden
        holder.costoOrdenItem.text = costo
        holder.estadoOrdenItem.text = estadoOrden

        if (estadoOrden.equals("Solicitud recibida")){
            holder.estadoOrdenItem.setTextColor(ContextCompat.getColor(mContext, R.color.azul_marino_oscuro))
        }else if (estadoOrden.equals("Pago Pendiente")){
            holder.estadoOrdenItem.setTextColor(ContextCompat.getColor(mContext, R.color.morado))
        }
        else if (estadoOrden.equals("En Preparación")){
            holder.estadoOrdenItem.setTextColor(ContextCompat.getColor(mContext, R.color.naranja))
        }else if (estadoOrden.equals("Entregado")){
            holder.estadoOrdenItem.setTextColor(ContextCompat.getColor(mContext, R.color.verde_oscuro2))
        }else if (estadoOrden.equals("Cancelado")){
            holder.estadoOrdenItem.setTextColor(ContextCompat.getColor(mContext, R.color.rojo))
        }

        val fecha = Constantes().obtenerFecha(tiempoOrden.toLong())

        binding.fechaOrdenItem.text = fecha

        holder.ibSiguiente.setOnClickListener {
            val intent = Intent(mContext, DetalleOrdenCActivity::class.java)
            intent.putExtra("idOrden", idOrden)
            mContext.startActivity(intent)
        }

    }

    inner class HolderOrdenCompra (itemView : View) : RecyclerView.ViewHolder(itemView){
        var idOrdenItem = binding.idOrdenItem
        var fechaOrdenItem = binding.fechaOrdenItem
        var estadoOrdenItem = binding.estadoOrdenItem
        var costoOrdenItem = binding.costoOrdenItem
        var ibSiguiente = binding.ibSiguiente
    }


}