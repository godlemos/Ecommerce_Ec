package com.example.tiendavirtualapp_kotlin2.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dinocode.tiendavirtualapp_kotlin.Modelos.ModeloCalificacion
import com.example.tiendavirtualapp_kotlin2.databinding.ItemCalificacionBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorCalificacion : RecyclerView.Adapter<AdaptadorCalificacion.HolderCalificacion>{

    private lateinit var binding : ItemCalificacionBinding

    private var mContext : Context
    private var calificacionArrayList : ArrayList<ModeloCalificacion>

    constructor(mContext: Context, calificacionArrayList: ArrayList<ModeloCalificacion>) {
        this.mContext = mContext
        this.calificacionArrayList = calificacionArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCalificacion {
        binding = ItemCalificacionBinding.inflate(LayoutInflater.from(mContext), parent , false)
        return HolderCalificacion(binding.root)
    }

    override fun getItemCount(): Int {
        return calificacionArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCalificacion, position: Int) {
        val modelo = calificacionArrayList[position]

        val uidUsuario = modelo.uidUsuario
        val resenia = modelo.resenia
        val calificacion = modelo.calificacion

        cargarNombre(uidUsuario , holder)
        holder.item_resenia.text = resenia
        holder.item_ratingBar.rating = calificacion.toFloat()
    }

    private fun cargarNombre(uidUsuario: String , holder : AdaptadorCalificacion.HolderCalificacion) {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uidUsuario).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val nombre = "${snapshot.child("nombres").value}"
                holder.item_nombre_usuario.text = nombre
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    inner class HolderCalificacion (itemView : View) : RecyclerView.ViewHolder(itemView){
        var item_nombre_usuario = binding.tvNombreUsuarioCal
        var item_ratingBar = binding.ratingBar
        var item_resenia = binding.tvReseniaCal
    }


}