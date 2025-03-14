package com.example.tiendavirtualapp_kotlin2.Adaptadores

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloProducto
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.Vendedor.MainActivityVendedor
import com.example.tiendavirtualapp_kotlin2.Vendedor.Productos.AgregarProductoActivity
import com.example.tiendavirtualapp_kotlin2.databinding.ItemProductoBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorProducto : RecyclerView.Adapter<AdaptadorProducto.HolderProducto> {

    private lateinit var binding : ItemProductoBinding

    private var mContex : Context
    private var productosArrayList : ArrayList<ModeloProducto>

    constructor(mContex: Context, productosArrayList: ArrayList<ModeloProducto>) {
        this.mContex = mContex
        this.productosArrayList = productosArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProducto {
        binding = ItemProductoBinding.inflate(LayoutInflater.from(mContex), parent, false)
        return HolderProducto(binding.root)
    }

    override fun getItemCount(): Int {
        return productosArrayList.size
    }

    override fun onBindViewHolder(holder: HolderProducto, position: Int) {
        val modeloProducto = productosArrayList[position]

        val nombre = modeloProducto.nombre
        val categoria = modeloProducto.categoria

        cargarPrimeraImg(modeloProducto, holder)
        visualizarDescuento(modeloProducto , holder)

        holder.item_nombre_p.text = "${nombre}"
        holder.item_categoria_p.text = "${categoria}"

        holder.Ib_editar.setOnClickListener {
            val intent = Intent(mContex, AgregarProductoActivity::class.java)
            intent.putExtra("Edicion", true)
            intent.putExtra("idProducto", modeloProducto.id)
            mContex.startActivity(intent)
        }

        holder.Ib_eliminar.setOnClickListener {
            val mAlertDialog = MaterialAlertDialogBuilder(mContex)
            mAlertDialog.setTitle("Eliminar producto")
                .setMessage("¿Estás seguro(a) de eliminar el producto?")
                .setPositiveButton("Eliminar"){dialog,which->
                    eliminarProductoBD(modeloProducto)
                }
                .setNegativeButton("Cancelar"){dialog,which->
                    dialog.dismiss()
                }
                .show()

        }

    }

    private fun eliminarProductoBD(modeloProducto: ModeloProducto) {
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(modeloProducto.id)
            .removeValue()
            .addOnSuccessListener {
                val intent = Intent(mContex, MainActivityVendedor::class.java)
                mContex.startActivity(intent)
                Toast.makeText(mContex, "Producto eliminado",Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e->
                Toast.makeText(mContex, "${e.message}",Toast.LENGTH_SHORT).show()
            }

    }

    private fun visualizarDescuento(modeloProducto: ModeloProducto , holder: AdaptadorProducto.HolderProducto) {
        if (!modeloProducto.precioDescuento.equals("0") && modeloProducto.descuento > 0){
            //Habilitar las vistas
            holder.item_nota_p.visibility = View.VISIBLE
            holder.item_precio_p_desc.visibility = View.VISIBLE

            //Setear la información
            holder.item_nota_p.text = "${modeloProducto.descuento}% OFF"
            holder.item_precio_p_desc.text = "${modeloProducto.precioDescuento}${" USD"}"
            holder.item_precio_p.text = "${modeloProducto.precio}${" USD"}"
            holder.item_precio_p.paintFlags = holder.item_precio_p.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG //Tachando el precio original
        }else{
            //El producto no tiene descuento
            //Ocultar las vistas
            holder.item_nota_p.visibility = View.GONE
            holder.item_precio_p_desc.visibility = View.GONE

            //Setear la información
            holder.item_precio_p.text = "${modeloProducto.precio}${" USD"}"
            holder.item_precio_p.paintFlags = holder.item_precio_p.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv() //Quitar el tachado
        }
    }

    private fun cargarPrimeraImg(modeloProducto: ModeloProducto, holder: AdaptadorProducto.HolderProducto) {
        val idProducto = modeloProducto.id

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto).child("Imagenes")
            .limitToFirst(1)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children){
                        val imagenUrl = "${ds.child("imagenUrl").value}"

                        try {
                            Glide.with(mContex)
                                .load(imagenUrl)
                                .placeholder(R.drawable.item_img_producto)
                                .into(holder.imagenP)
                        }catch (e:Exception){

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    inner class HolderProducto(itemView : View) : RecyclerView.ViewHolder(itemView){
        var imagenP = binding.imagenP
        var item_nombre_p = binding.itemNombreP
        var item_categoria_p = binding.itemCategoriaP
        var item_precio_p = binding.itemPrecioP
        var item_precio_p_desc = binding.itemPrecioPDesc
        var item_nota_p = binding.itemNotaP
        var Ib_editar = binding.IbEditar
        var Ib_eliminar = binding.IbEliminar
    }



}