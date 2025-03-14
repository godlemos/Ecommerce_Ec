package com.example.tiendavirtualapp_kotlin2.Cliente.Bottom_Nav_Fragments_Cliente

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tiendavirtualapp_kotlin2.Adaptadores.AdaptadorProductoC
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloProducto
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.databinding.FragmentFavoritosCBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FragmentFavoritosC : Fragment() {

    private lateinit var binding : FragmentFavoritosCBinding

    private lateinit var mContext : Context
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var productosArrayList : ArrayList<ModeloProducto>
    private lateinit var productoAdaptador : AdaptadorProductoC

    override fun onAttach(context: Context) {
        this.mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavoritosCBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        cargarProdFav()
    }

    private fun cargarProdFav() {
        productosArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    productosArrayList.clear()
                    for (ds in snapshot.children){
                        val idProducto = "${ds.child("idProducto").value}"

                        val refProd = FirebaseDatabase.getInstance().getReference("Productos")
                        refProd.child(idProducto)
                            .addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    try {
                                        val modeloProducto = snapshot.getValue(ModeloProducto::class.java)
                                        productosArrayList.add(modeloProducto!!)

                                    }catch (e:Exception){

                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })
                    }

                    Handler().postDelayed({
                        productosArrayList.sortBy { it.nombre }
                        productoAdaptador =AdaptadorProductoC(mContext, productosArrayList)
                        binding.favoritosRv.adapter = productoAdaptador
                    }, 500)

                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }


}