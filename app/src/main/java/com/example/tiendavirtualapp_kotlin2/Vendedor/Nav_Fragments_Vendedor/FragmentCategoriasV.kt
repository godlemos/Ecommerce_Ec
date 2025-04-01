package com.example.tiendavirtualapp_kotlin2.Vendedor.Nav_Fragments_Vendedor

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.databinding.FragmentCategoriasVBinding
import com.example.tiendavirtualapp_kotlin2.databinding.FragmentInicioVBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tiendavirtualapp_kotlin2.Adaptadores.AdaptadorCategoriaV
import com.example.tiendavirtualapp_kotlin2.Modelos.Categoria
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import androidx.appcompat.app.AlertDialog
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloCategoria

class FragmentCategoriasV : Fragment() {

    private lateinit var binding : FragmentCategoriasVBinding
    private lateinit var mContext: Context
    private lateinit var progressDialog : ProgressDialog
    private var imageUri : Uri?=null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var categoriasList: ArrayList<ModeloCategoria>
    private lateinit var adaptador: AdaptadorCategoriaV

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategoriasVBinding.inflate(inflater, container, false)

        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.imgCategorias.setOnClickListener {
            seleccionarImg()
        }

        binding.btnAgregarCat.setOnClickListener {
            validarDatos()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        firebaseAuth = FirebaseAuth.getInstance()
        cargarCategorias()
    }

    private fun seleccionarImg() {
        ImagePicker.with(requireActivity())
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent {intent ->
                resultadoImg.launch(intent)
                
            }
    }
    private val resultadoImg =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){resultado->
            if (resultado.resultCode == Activity.RESULT_OK){
                val data = resultado.data
                imageUri = data!!.data
                binding.imgCategorias.setImageURI(imageUri)
            }else{
                Toast.makeText(mContext, "Accion cancelada",Toast.LENGTH_SHORT).show()
            }

        }

    private fun cargarCategorias() {
        categoriasList = ArrayList()
        adaptador = AdaptadorCategoriaV(requireContext(), categoriasList)
        
        // Configurar RecyclerView
        binding.rvCategorias.layoutManager = LinearLayoutManager(context)
        binding.rvCategorias.adapter = adaptador

        // Configurar click listeners
        adaptador.setOnDeleteClickListener { categoria ->
            eliminarCategoria(categoria)
        }

        // Obtener categorías de Firebase
        val ref = FirebaseDatabase.getInstance().getReference("Categorias")
        ref.orderByChild("uid").equalTo(firebaseAuth.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    categoriasList.clear()
                    for (ds in snapshot.children) {
                        val categoria = ds.getValue(ModeloCategoria::class.java)
                        categoria?.let { categoriasList.add(it) }
                    }
                    adaptador.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error al cargar categorías: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun validarDatos() {
        val categoria = binding.etCategoria.text.toString().trim()
        
        // Verificar si se seleccionó una imagen
        if (imageUri == null) {
            Toast.makeText(context, "Elige una imagen para la categoría", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar si se ingresó el nombre de la categoría
        if (categoria.isEmpty()) {
            binding.etCategoria.error = "Ingrese una categoría"
            return
        }

        // Si todo está correcto, proceder a agregar la categoría
        val timestamp = System.currentTimeMillis()
        val uid = firebaseAuth.uid ?: return

        progressDialog.setMessage("Agregando categoría")
        progressDialog.show()
        
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["categoria"] = categoria
        hashMap["uid"] = uid
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Categorias")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                // Después de agregar los datos básicos, subir la imagen
                subirImgStorage("$timestamp")
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(context, "Error al agregar categoría: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun eliminarCategoria(categoria: ModeloCategoria) {
        // Crear el diálogo de confirmación
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Eliminar Categoría")
            .setMessage("¿Estás seguro(a) de eliminar esta categoría?")
            .setPositiveButton("Confirmar") { _, _ ->
                // Si el usuario confirma, procedemos a eliminar
                val ref = FirebaseDatabase.getInstance().getReference("Categorias")
                ref.child(categoria.id)
                    .removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Categoría eliminada", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error al eliminar categoría: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun subirImgStorage(keyId: String) {
        progressDialog.setMessage("Subiendo imagen")
        progressDialog.show()

        val nombreImagen = keyId
        val nombreCarpeta = "Categorias/$nombreImagen"
        val storageReference = FirebaseStorage.getInstance().getReference(nombreCarpeta)
        storageReference.putFile(imageUri!!)
            .addOnSuccessListener {taskSnapshot->
                progressDialog.dismiss()
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val urlImgCargada = uriTask.result

                if (uriTask.isSuccessful){
                    val hashMap = HashMap<String, Any>()
                    hashMap["imagenUrl"] = "$urlImgCargada"
                    val ref = FirebaseDatabase.getInstance().getReference("Categorias")
                    ref.child(nombreImagen).updateChildren(hashMap)
                    Toast.makeText(mContext, "Se agrego la categoria con exito", Toast.LENGTH_SHORT).show()
                    binding.etCategoria.setText("")
                    imageUri = null
                    binding.imgCategorias.setImageURI(imageUri)
                    binding.imgCategorias.setImageResource(R.drawable.categorias)
                }

            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()

            }







    }


}