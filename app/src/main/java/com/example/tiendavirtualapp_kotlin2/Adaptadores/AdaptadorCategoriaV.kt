package com.example.tiendavirtualapp_kotlin2.Adaptadores

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloCategoria
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.Vendedor.Productos.ProductosCatVActivity
import com.example.tiendavirtualapp_kotlin2.databinding.ItemCategoriaVBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AdaptadorCategoriaV : RecyclerView.Adapter<AdaptadorCategoriaV.HolderCategoriaV> {

    private lateinit var binding : ItemCategoriaVBinding

    private val mContext : Context
    private val categoriaArrayList : ArrayList<ModeloCategoria>
    private var onDeleteClickListener: ((ModeloCategoria) -> Unit)? = null

    constructor(mContext: Context, categoriaArrayList: ArrayList<ModeloCategoria>) {
        this.mContext = mContext
        this.categoriaArrayList = categoriaArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoriaV {
        binding = ItemCategoriaVBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return HolderCategoriaV(binding.root)
    }

    override fun getItemCount(): Int {
        return categoriaArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategoriaV, position: Int) {
        val modelo = categoriaArrayList[position]

        val id = modelo.id
        val categoria = modelo.categoria
        val imagen = modelo.imagenUrl

        holder.item_nombre_c_v.text = categoria

        Glide.with(mContext)
            .load(imagen)
            .placeholder(R.drawable.categorias)
            .into(holder.item_img_c_v)



        holder.item_eliminar_c.setOnClickListener {
            //Toast.makeText(mContext, "Eliminar categoria",Toast.LENGTH_SHORT).show()
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("Eliminar categoria")
            builder.setMessage("¿Estás seguro(a) de eliminar esta categoría?")
                .setPositiveButton("Confirmar"){a,d->
                    eliminarCategoria(modelo, holder)
                }
                .setNegativeButton("Cancelar"){a,d->
                    a.dismiss()
                }
            builder.show()
        }

        holder.item_ver_productos.setOnClickListener {
            val intent = Intent(mContext , ProductosCatVActivity::class.java)
            intent.putExtra("nombreCat", categoria)
            Toast.makeText(mContext, "Categoría seleccionada ${categoria}",Toast.LENGTH_SHORT).show()
            mContext.startActivity(intent)
        }
    }

    private fun eliminarCategoria(modelo: ModeloCategoria, holder: AdaptadorCategoriaV.HolderCategoriaV) {
        val idCat = modelo.id
        val ref = FirebaseDatabase.getInstance().getReference("Categorias")
        ref.child(idCat).removeValue()
            .addOnSuccessListener {
                Toast.makeText(mContext, "Categoría eliminada",Toast.LENGTH_SHORT).show()
                eliminarImgCat(idCat)
            }
            .addOnFailureListener {e->
                Toast.makeText(mContext, "No se eliminó la categoria debido a ${e.message} ",Toast.LENGTH_SHORT).show()
            }
    }

    private fun eliminarImgCat(idCat: String){
        val nombreImg = idCat
        val rutaImagen = "Categorias/$nombreImg"
        val storageRef = FirebaseStorage.getInstance().getReference(rutaImagen)
        storageRef.delete()
            .addOnSuccessListener {
                Toast.makeText(mContext, "Se eliminó la imagen de la categoría",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(mContext, "${e.message} ",Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarNomCat(id : String){
        val etNuevoNomCat : EditText
        val btnActualizarNomCat : MaterialButton
        val ibCerrar : ImageButton

        val dialog = Dialog(mContext)

        dialog.setContentView(R.layout.dialog_act_nom_cat)

        etNuevoNomCat = dialog.findViewById(R.id.etNuevoNomCat)
        btnActualizarNomCat = dialog.findViewById(R.id.btnActualizarNomCat)
        ibCerrar = dialog.findViewById(R.id.ibCerrar)

        btnActualizarNomCat.setOnClickListener {
            var nuevoNombre = etNuevoNomCat.text.toString().trim()
            if (nuevoNombre.isNotEmpty()){
                actualizarNomCatBD(id , nuevoNombre)
                dialog.dismiss()
            }else{
                Toast.makeText(mContext, "Ingrese un nombre",Toast.LENGTH_SHORT).show()
            }
        }

        ibCerrar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
    }

    private fun actualizarNomCatBD(idCat: String , nuevoNombre : String){
        val hashMap = HashMap<String , Any>()
        hashMap["categoria"] = nuevoNombre

        val ref = FirebaseDatabase.getInstance().getReference("Categorias").child(idCat)
        ref.updateChildren(hashMap)
            .addOnSuccessListener {
                Toast.makeText(mContext, "Nombre actualizado",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(mContext, "Ha ocurrido un error debido a:  ${e.message}",Toast.LENGTH_SHORT).show()

            }
    }

    fun setOnDeleteClickListener(listener: (ModeloCategoria) -> Unit) {
        onDeleteClickListener = listener
    }

    inner class HolderCategoriaV(itemView : View) : RecyclerView.ViewHolder(itemView){
        var item_nombre_c_v = binding.itemNombreCV
        var item_eliminar_c = binding.itemEliminarC
        var item_img_c_v = binding.imagenCategCV
        var item_ver_productos = binding.itemVerProductos
    }

}