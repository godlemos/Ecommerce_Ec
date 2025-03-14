package com.example.tiendavirtualapp_kotlin2.Adaptadores

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloProductoCarrito
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.databinding.ItemCarritoCBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorCarritoC : RecyclerView.Adapter<AdaptadorCarritoC.HolderProductoCarrito> {

    private lateinit var binding : ItemCarritoCBinding

    private var mContext : Context
    var productosArrayList : ArrayList<ModeloProductoCarrito>
    private var firebaseAuth : FirebaseAuth

    constructor(
        mContext: Context,
        productosArrayList: ArrayList<ModeloProductoCarrito>
    ) : super() {
        this.mContext = mContext
        this.productosArrayList = productosArrayList
        this.firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProductoCarrito {
        binding = ItemCarritoCBinding.inflate(LayoutInflater.from(mContext),parent, false)
        return HolderProductoCarrito(binding.root)
    }

    override fun getItemCount(): Int {
        return productosArrayList.size
    }

    var costo : Double = 0.0
    override fun onBindViewHolder(holder: HolderProductoCarrito, position: Int) {
        val modeloProductoCarrito = productosArrayList[position]

        val nombre = modeloProductoCarrito.nombre
        var cantidad = modeloProductoCarrito.cantidad
        var precioFinal = modeloProductoCarrito.precioFinal
        var precio = modeloProductoCarrito.precio
        var precioDesc = modeloProductoCarrito.precioDescuento

        holder.nombrePCar.text = nombre
        holder.cantidadPCar.text = cantidad.toString()

        cargarPrimeraImg(modeloProductoCarrito, holder)

        visualizarDescuento(modeloProductoCarrito , holder)

        holder.btnEliminar.setOnClickListener {
            eliminarProdCarrito(mContext , modeloProductoCarrito.idProducto)
        }

        var miPrecioFinalDouble = if (precioFinal.isNotEmpty()) {
            try {
                precioFinal.toDouble()
            } catch (e: NumberFormatException) {
                0.0
            }
        } else {
            0.0
        }

        holder.btnAumentar.setOnClickListener {
            try {
                if (!precioDesc.equals("0") && precioDesc.isNotEmpty()){
                    costo = precioDesc.toDouble()
                } else if (precio.isNotEmpty()) {
                    costo = precio.toDouble()
                } else {
                    costo = 0.0
                    Toast.makeText(mContext, "Error: Precio no disponible", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                miPrecioFinalDouble += costo
                cantidad++

                holder.cantidadPCar.text = cantidad.toString()
                
                holder.precioFinalPCar.text = miPrecioFinalDouble.toString().plus(" USD")

                var precioFinalString = miPrecioFinalDouble.toString()

                calcularNuevoPrecio(mContext , modeloProductoCarrito.idProducto , precioFinalString , cantidad)
            } catch (e: Exception) {
                Toast.makeText(mContext, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        holder.btnDisminuir.setOnClickListener {
            if (cantidad > 1){
                try {
                    if (!precioDesc.equals("0") && precioDesc.isNotEmpty()){
                        costo = precioDesc.toDouble()
                    } else if (precio.isNotEmpty()) {
                        costo = precio.toDouble()
                    } else {
                        costo = 0.0
                        Toast.makeText(mContext, "Error: Precio no disponible", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    miPrecioFinalDouble = miPrecioFinalDouble - costo
                    cantidad--

                    holder.cantidadPCar.text = cantidad.toString()
                    
                    holder.precioFinalPCar.text = miPrecioFinalDouble.toString().plus(" USD")

                    var precioFinalString = miPrecioFinalDouble.toString()

                    calcularNuevoPrecio(mContext , modeloProductoCarrito.idProducto , precioFinalString , cantidad)
                } catch (e: Exception) {
                    Toast.makeText(mContext, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun calcularNuevoPrecio(mContext: Context, idProducto: String, precioFinalString: String, cantidad: Int) {
        val hashMap : HashMap<String , Any> = HashMap()

        hashMap["cantidad"] = cantidad
        hashMap["precioFinal"] = precioFinalString

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("CarritoCompras").child(idProducto)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                // Actualización exitosa
                Toast.makeText(mContext, "Se actualizó la cantidad", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                // Error al actualizar
                Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun eliminarProdCarrito(mContext: Context, idProducto: String) {
        val firebaseAuth = FirebaseAuth.getInstance()

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("CarritoCompras").child(idProducto)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(mContext , "Producto eliminado del carrito", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(mContext , "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun visualizarDescuento(modeloProductoCarrito: ModeloProductoCarrito, holder: AdaptadorCarritoC.HolderProductoCarrito) {
        try {
            // Verificar que los precios no sean cadenas vacías
            if (modeloProductoCarrito.precioDescuento.isEmpty() || modeloProductoCarrito.precio.isEmpty() || modeloProductoCarrito.precioFinal.isEmpty()) {
                // Si alguno de los precios es vacío, mostrar solo el precio final sin tachar
                holder.precioOriginalPCar.visibility = View.GONE
                
                // Usar un valor predeterminado si el precio final es vacío
                val precioFinalTexto = if (modeloProductoCarrito.precioFinal.isNotEmpty()) {
                    modeloProductoCarrito.precioFinal.plus(" USD")
                } else {
                    "0.0 USD"
                }
                
                holder.precioFinalPCar.text = precioFinalTexto
                return
            }
            
            // Verificar si hay un descuento real (precioDescuento diferente de "0" y diferente del precio original)
            if (!modeloProductoCarrito.precioDescuento.equals("0") && !modeloProductoCarrito.precioDescuento.equals(modeloProductoCarrito.precio)){
                // Producto con descuento
                holder.precioFinalPCar.text = modeloProductoCarrito.precioFinal.plus(" USD")
                holder.precioOriginalPCar.visibility = View.VISIBLE
                holder.precioOriginalPCar.text = modeloProductoCarrito.precio.plus(" USD")
                holder.precioOriginalPCar.paintFlags = holder.precioOriginalPCar.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                // Producto sin descuento
                holder.precioOriginalPCar.visibility = View.GONE
                holder.precioFinalPCar.text = modeloProductoCarrito.precioFinal.plus(" USD")
            }
        } catch (e: Exception) {
            // En caso de error, mostrar solo el precio final sin tachar
            holder.precioOriginalPCar.visibility = View.GONE
            holder.precioFinalPCar.text = "0.0 USD"
        }
    }

    private fun cargarPrimeraImg(modeloProductoCarrito: ModeloProductoCarrito, holder: AdaptadorCarritoC.HolderProductoCarrito) {
        val idProducto = modeloProductoCarrito.idProducto

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto).child("Imagenes")
            .limitToFirst(1)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children){
                        val imagenUrl = "${ds.child("imagenUrl").value}"

                        try {
                            Glide.with(mContext)
                                .load(imagenUrl)
                                .placeholder(R.drawable.item_img_producto)
                                .into(holder.imagenPCar)
                        }catch (e:Exception){

                        }
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }

    inner class HolderProductoCarrito(itemView : View) : RecyclerView.ViewHolder(itemView){
        var imagenPCar = binding.imagenPCar
        var nombrePCar = binding.nombrePCar
        var precioFinalPCar = binding.precioFinalPCar
        var precioOriginalPCar = binding.precioOriginalPCar
        var btnDisminuir = binding.btnDisminuir
        var cantidadPCar = binding.cantidadPCar
        var btnAumentar = binding.btnAumentar
        var btnEliminar = binding.btnEliminar
    }


}