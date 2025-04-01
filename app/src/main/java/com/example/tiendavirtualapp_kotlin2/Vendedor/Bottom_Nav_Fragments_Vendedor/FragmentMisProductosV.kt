package com.example.tiendavirtualapp_kotlin2.Vendedor.Bottom_Nav_Fragments_Vendedor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.databinding.FragmentMisProductosVBinding
import android.content.Context
import com.example.tiendavirtualapp_kotlin2.Adaptadores.AdaptadorProducto
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloProducto
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FragmentMisProductosV : Fragment() {

    private lateinit var binding: FragmentMisProductosVBinding
    private  lateinit var mContext : Context

    private lateinit var productoArrayList : ArrayList<ModeloProducto>
    private lateinit var adaptadorProductos : AdaptadorProducto

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMisProductosVBinding.inflate(LayoutInflater.from(mContext), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listarProductos()


    }

    private fun listarProductos() {
        productoArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                productoArrayList.clear()
                for (ds in snapshot.children){
                    val  modeloProducto = ds.getValue(ModeloProducto::class.java)
                    productoArrayList.add(modeloProducto!!)
                }
                adaptadorProductos = AdaptadorProducto(mContext, productoArrayList)
                binding.categoriasRV.adapter = adaptadorProductos
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


}