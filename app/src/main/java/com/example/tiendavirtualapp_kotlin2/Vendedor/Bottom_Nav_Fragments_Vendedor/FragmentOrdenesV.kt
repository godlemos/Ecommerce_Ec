package com.example.tiendavirtualapp_kotlin2.Vendedor.Bottom_Nav_Fragments_Vendedor

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import com.example.tiendavirtualapp_kotlin2.Adaptadores.AdaptadorOrdenCompraV
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloOrdenCompra
import com.example.tiendavirtualapp_kotlin2.databinding.FragmentOrdenesVBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentOrdenesV : Fragment() {

    private lateinit var binding : FragmentOrdenesVBinding
    private lateinit var mContext : Context

    private lateinit var ordenesArrayList : ArrayList<ModeloOrdenCompra>
    private lateinit var ordenAdaptador : AdaptadorOrdenCompraV

    override fun onAttach(context: Context) {
        this.mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentOrdenesVBinding.inflate(inflater, container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        verOrdenes()

        binding.IbFiltroEstado.setOnClickListener {
            filtrarOrdenMenu()
        }
    }

    private fun filtrarOrdenMenu() {
        val popupMenu = PopupMenu(mContext , binding.IbFiltroEstado)

        popupMenu.menu.add(Menu.NONE, 0 , 0 , "Todos")
        popupMenu.menu.add(Menu.NONE, 1 , 1 , "Solicitud Recibida")
        popupMenu.menu.add(Menu.NONE, 2 , 2 , "Pago Pendiente")
        popupMenu.menu.add(Menu.NONE, 3 , 3 , "En preparación")
        popupMenu.menu.add(Menu.NONE, 4 , 4 , "Entregado")
        popupMenu.menu.add(Menu.NONE, 5 , 5 , "Cancelado")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item->
            val itemId = item.itemId

            when(itemId){
                0-> verOrdenes()
                1-> filtroOrden("Solicitud recibida")
                2-> filtroOrden("Pago Pendiente")
                3-> filtroOrden("En preparación")
                4-> filtroOrden("Entregado")
                5-> filtroOrden("Cancelado")
            }

            return@setOnMenuItemClickListener true
        }
    }

    private fun filtroOrden(estado : String) {
        ordenesArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Ordenes")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                ordenesArrayList.clear()

                for (ds in snapshot.children){

                    val modeloOrden = ds.getValue(ModeloOrdenCompra::class.java)
                    when(modeloOrden?.estadoOrden){
                        //Filtrar orden según el estado
                        estado -> ordenesArrayList.add(modeloOrden)
                    }
                }

                ordenAdaptador = AdaptadorOrdenCompraV(mContext, ordenesArrayList)
                binding.ordenesRv.adapter = ordenAdaptador
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error
            }
        })
    }

    private fun verOrdenes() {
        ordenesArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Ordenes")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                ordenesArrayList.clear()
                for (ds in snapshot.children){
                    val modelo = ds.getValue(ModeloOrdenCompra::class.java)
                    ordenesArrayList.add(modelo!!)
                }

                ordenAdaptador = AdaptadorOrdenCompraV(mContext , ordenesArrayList)
                binding.ordenesRv.adapter = ordenAdaptador
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error
            }
        })
    }
}