package com.example.tiendavirtualapp_kotlin2.Vendedor.Nav_Fragments_Vendedor

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tiendavirtualapp_kotlin2.Adaptadores.AdaptadorProducto
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloProducto
import com.example.tiendavirtualapp_kotlin2.databinding.FragmentProductosVBinding
import com.google.android.gms.common.internal.Objects
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentProductosV : Fragment() {

    private lateinit var binding : FragmentProductosVBinding
    private lateinit var mContext : Context

    private lateinit var productoArrayList : ArrayList<ModeloProducto>
    private lateinit var adaptadorProductos : AdaptadorProducto

    private val cantidadProductos = 4
    private var ultimoProductoVisible : DataSnapshot ?= null
    private var primerProductoVisible : DataSnapshot ?= null

    private var cargandoDatos = false
    private var primeraPagina = true

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable : Runnable?= null

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductosVBinding.inflate(inflater, container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listarProductos()

        binding.etBuscarProducto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(nombreP: CharSequence?, start: Int, before: Int, count: Int) {
                val nombreProducto = nombreP.toString()

                searchRunnable = Runnable {
                    if (nombreProducto.isNotEmpty()){
                        /*Buscar un producto*/
                        buscarProducto(nombreProducto)
                    }else{
                        /*Mostrar todos los productos*/
                        listarProductos()
                    }
                }

                handler.postDelayed(searchRunnable!! , 1000)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.btnAnterior.isEnabled = false

        binding.btnAnterior.setOnClickListener {
            if (!cargandoDatos && !primeraPagina){
                cargarPaginaAnterior()
            }
        }

        binding.btnSiguiente.setOnClickListener {
            if (!cargandoDatos){
                cargarPaginaSiguiente()
            }
        }

        productoArrayList = ArrayList()
        adaptadorProductos = AdaptadorProducto(mContext , productoArrayList)
        binding.productosRV.adapter = adaptadorProductos

        binding.productosRV.layoutManager = GridLayoutManager(mContext , 2)

        listarProductos()
    }

    private fun cargarPaginaSiguiente() {
        listarProductos()
        primeraPagina = false
        binding.btnAnterior.isEnabled = true
    }

    private fun cargarPaginaAnterior() {
        if (primerProductoVisible!=null){
            cargandoDatos = true

            val ref = FirebaseDatabase.getInstance().getReference("Productos")

            var query = ref.orderByKey().endBefore(primerProductoVisible!!.key).limitToLast(cantidadProductos)

            query.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    productoArrayList.clear()
                    if (snapshot.hasChildren()){
                        primerProductoVisible = snapshot.children.first()
                        ultimoProductoVisible = snapshot.children.last()

                        for (ds in snapshot.children){
                            val modeloProducto = ds.getValue(ModeloProducto::class.java)
                            if (modeloProducto!=null){
                                productoArrayList.add(modeloProducto)
                            }
                        }

                        adaptadorProductos.notifyDataSetChanged()

                        comprobarPrimeraPagina()

                    }

                    cargandoDatos = false
                }

                override fun onCancelled(error: DatabaseError) {
                    cargandoDatos = false
                }
            })
        }
    }

    private fun comprobarPrimeraPagina() {
        val ref = FirebaseDatabase.getInstance().getReference("Productos")

        ref.orderByKey().limitToFirst(1).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    /*Obtener el primer producto de la BD*/
                    val primerProdBD = snapshot.children.first()

                    /*Verificar si el primer elemento de BD es igual al primerProductoVisible*/
                    primeraPagina = primerProductoVisible?.key == primerProdBD.key /*true*/

                    binding.btnAnterior.isEnabled = !primeraPagina /*false*/
                    binding.btnSiguiente.isEnabled = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun buscarProducto(nombreProducto: String) {
        productoArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
            .orderByChild("nombre").startAt(nombreProducto).endAt(nombreProducto + "\uf8ff")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    /*Si el producto existe*/
                    for (ds in snapshot.children){
                        val producto = ds.getValue(ModeloProducto::class.java)
                        productoArrayList.add(producto!!)
                    }

                    adaptadorProductos = AdaptadorProducto(mContext , productoArrayList)
                    binding.productosRV.adapter = adaptadorProductos
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun listarProductos() {
        cargandoDatos = true /*La carga de datos ha empezado*/

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        var query = ref.orderByKey().limitToFirst(cantidadProductos)

        if (ultimoProductoVisible != null){
            /*Si No estamos en la primera página*/
            /*Esta consulta nos indica que se debe empezar a recuperar datos después de la clave especificada
            * que en este caso es ultiProductoVisible*/
            query = ref.orderByKey().startAfter(ultimoProductoVisible!!.key).limitToFirst(cantidadProductos)
            primeraPagina = false
        }else{
            /*Estamos en la primera página*/
            primeraPagina = true
        }

        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                productoArrayList.clear()

                if (snapshot.hasChildren()){
                    primerProductoVisible = snapshot.children.first() /*Almacenamos el primer producto visible*/
                    ultimoProductoVisible = snapshot.children.last()  /*Almacenamos el último producto visible*/

                    for (ds in snapshot.children){

                        val modeloProducto = ds.getValue(ModeloProducto::class.java)
                        if (modeloProducto != null){
                            productoArrayList.add(modeloProducto)
                        }
                    }

                    adaptadorProductos.notifyDataSetChanged()

                    binding.btnAnterior.isEnabled = !primeraPagina
                    binding.btnSiguiente.isEnabled = snapshot.childrenCount.toInt() == cantidadProductos

                }else{
                    /*Deshabilitar el botón de siguiente si no hay más productos*/
                    binding.btnSiguiente.isEnabled = false
                }

                cargandoDatos = false /*La carga de datos ha terminado*/
            }

            override fun onCancelled(error: DatabaseError) {
                cargandoDatos = false /*La carga de datos ha terminado*/
            }
        })









    }

}