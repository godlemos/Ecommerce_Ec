package com.example.tiendavirtualapp_kotlin2.Cliente.Bottom_Nav_Fragments_Cliente

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.tiendavirtualapp_kotlin2.Adaptadores.AdaptadorCarritoC
import com.example.tiendavirtualapp_kotlin2.Cliente.Orden.DetalleOrdenCActivity
import com.example.tiendavirtualapp_kotlin2.Constantes
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloProductoCarrito
import com.example.tiendavirtualapp_kotlin2.databinding.FragmentCarritoCBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentCarritoC : Fragment() {

    private lateinit var binding : FragmentCarritoCBinding

    private lateinit var mContext : Context
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var productosArrayList : ArrayList<ModeloProductoCarrito>
    private lateinit var productoAdaptadorCarritoC: AdaptadorCarritoC

    override fun onAttach(context: Context) {
        this.mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCarritoCBinding.inflate(inflater, container , false)

        binding.btnCrearOrden.setOnClickListener {
            if (productosArrayList.size == 0){
                Toast.makeText(mContext, "No hay productos en el carrito",Toast.LENGTH_SHORT).show()
            }else{
                crearOrden()
            }
        }

        return binding.root
    }

    private fun crearOrden() {
        val tiempo = Constantes().obtenerTiempoD()
        val costo = binding.sumaProductos.text.toString().trim()
        val uid = firebaseAuth.uid

        val ref = FirebaseDatabase.getInstance().getReference("Ordenes")
        val keyId = ref.push().key

        val hashMap = HashMap<String , Any> ()
        hashMap["idOrden"] = "${keyId}"
        hashMap["tiempoOrden"] = "${tiempo}"
        hashMap["estadoOrden"] = "Solicitud recibida"
        hashMap["costo"] = "${costo}"
        hashMap["ordenadoPor"] = "${uid}"

        ref.child(keyId!!).setValue(hashMap)
            .addOnSuccessListener {
                for (producto in productosArrayList){
                    val idProducto = producto.idProducto
                    val nombre = producto.nombre
                    val precio = producto.precio
                    val precioFinal = producto.precioFinal
                    val precioDesc = producto.precioDescuento
                    val cantidad = producto.cantidad

                    val hashMap2 = HashMap<String , Any>()
                    hashMap2["idProducto"] = idProducto
                    hashMap2["nombre"] = nombre
                    hashMap2["precio"] = precio
                    hashMap2["precioFinal"] = precioFinal
                    hashMap2["precioDescuento"] = precioDesc
                    hashMap2["cantidad"] = cantidad

                    ref.child(keyId).child("Productos").child(idProducto).setValue(hashMap2)
                    eliminarProductosCarrito()
                    binding.sumaProductos.text = ""
                }
                Toast.makeText(mContext , "Orden realizada con éxito",Toast.LENGTH_SHORT).show()
                val intent = Intent(mContext, DetalleOrdenCActivity::class.java)
                intent.putExtra("idOrden", keyId)
                startActivity(intent)
            }
            .addOnFailureListener { e->
                Toast.makeText(mContext , "${e.message}",Toast.LENGTH_SHORT).show()

            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        cargarProdCarrito()
        sumaProductos()
    }

    private fun eliminarProductosCarrito(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
            .child(uid!!).child("CarritoCompras")

        ref.removeValue().addOnCompleteListener {
            Toast.makeText(mContext, "Los productos se han eliminado del carrito",Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener {e->
                Toast.makeText(mContext, "${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun sumaProductos() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("CarritoCompras")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var suma = 0.0
                    for (producto in snapshot.children){

                        val precioFinal = producto.child("precioFinal").getValue(String::class.java)

                        if (precioFinal!=null){
                            suma += precioFinal.toDouble()
                        }

                        binding.sumaProductos.setText("Total: ${suma.toString().plus(" USD")}")

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun cargarProdCarrito() {

        productosArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("CarritoCompras")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    productosArrayList.clear()
                    for (ds in snapshot.children){
                        val modeloProductoCarrito = ds.getValue(ModeloProductoCarrito::class.java)
                        productosArrayList.add(modeloProductoCarrito!!)
                    }

                    productoAdaptadorCarritoC = AdaptadorCarritoC(mContext, productosArrayList)
                    binding.carritoRv.adapter = productoAdaptadorCarritoC
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })









    }


}