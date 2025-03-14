package com.example.tiendavirtualapp_kotlin2.Vendedor.Productos

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.tiendavirtualapp_kotlin2.Adaptadores.AdaptadorImagenSeleccionada
import com.example.tiendavirtualapp_kotlin2.Constantes
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloCategoria
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloImagenSeleccionada
import com.example.tiendavirtualapp_kotlin2.Vendedor.MainActivityVendedor
import com.example.tiendavirtualapp_kotlin2.databinding.ActivityAgregarProductoBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAgregarProductoBinding
    private var imagenUri : Uri?=null

    private lateinit var imagenSelecArrayList : ArrayList<ModeloImagenSeleccionada>
    private lateinit var adaptadorImagenSel : AdaptadorImagenSeleccionada

    private lateinit var categoriasArrayList : ArrayList<ModeloCategoria>

    private lateinit var progressDialog : ProgressDialog

    private var Edicion = false
    private var idProducto = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarCategorias()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        Edicion = intent.getBooleanExtra("Edicion", false)

        /*Estas vistas se inicializan como ocultas*/
        binding.etPorcentajeDescuentoP.visibility = View.GONE
        binding.btnCalcularPrecioDesc.visibility = View.GONE
        binding.precioConDescuentoPTXT.visibility = View.GONE
        binding.etPrecioConDescuentoP.visibility = View.GONE
        binding.etNotaDescuentoP.visibility = View.GONE

        binding.descuentoSwitch.setOnCheckedChangeListener{buttonView, isCheked->
            if (isCheked){
                //switch está habilitado
                binding.etPorcentajeDescuentoP.visibility = View.VISIBLE
                binding.btnCalcularPrecioDesc.visibility = View.VISIBLE
                binding.precioConDescuentoPTXT.visibility = View.VISIBLE
                binding.etPrecioConDescuentoP.visibility = View.VISIBLE
                binding.etNotaDescuentoP.visibility = View.VISIBLE
            }else{
                //switch está deshabilitado
                binding.etPorcentajeDescuentoP.visibility = View.GONE
                binding.btnCalcularPrecioDesc.visibility = View.GONE
                binding.precioConDescuentoPTXT.visibility = View.GONE
                binding.etPrecioConDescuentoP.visibility = View.GONE
                binding.etNotaDescuentoP.visibility = View.GONE
            }
        }

        if (Edicion){
            idProducto = intent.getStringExtra("idProducto") ?: ""
            binding.txtAgregarProductos.text = "Editar producto"
            cargarInfo()
        }else{
            binding.txtAgregarProductos.text = "Agregar un producto"
        }

        imagenSelecArrayList = ArrayList()

        binding.imgAgregarProducto.setOnClickListener {
            seleccionarImg()
        }

        binding.Categoria.setOnClickListener {
            selecCategorias()
        }

        binding.btnCalcularPrecioDesc.setOnClickListener {
            calcularPrecioDesc()
        }

        binding.btnAgregarProducto.setOnClickListener {
            validarInfo()
        }

        cargarImagenes()

    }

    private fun cargarInfo() {
        var ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                /*Obteniendo la información de Firebase*/
                val nombre = "${snapshot.child("nombre").value}"
                val descripcion = "${snapshot.child("descripcion").value}"
                val categoria = "${snapshot.child("categoria").value}"
                val precio = "${snapshot.child("precio").value}"
                val precioDesc = "${snapshot.child("precioDescuento").value}"
                val notaDesc = "${snapshot.child("notaDesc").value}"
                val porcentajeDesc = snapshot.child("descuento").getValue(Int::class.java) ?: 0

                /*Seteo de información*/
                binding.etNombresP.setText(nombre)
                binding.etDescripcionP.setText(descripcion)
                binding.Categoria.setText(categoria)
                binding.etPrecioP.setText(precio)
                binding.etPrecioConDescuentoP.setText(precioDesc)
                binding.etNotaDescuentoP.setText(notaDesc)
                binding.etPorcentajeDescuentoP.setText(porcentajeDesc.toString())
                
                // Activar el switch de descuento si hay un descuento
                if (porcentajeDesc > 0) {
                    binding.descuentoSwitch.isChecked = true
                }

                val refImagenes = snapshot.child("Imagenes").ref
                refImagenes.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (ds in snapshot.children){
                            val id = "${ds.child("id").value}"
                            val imagenUrl = "${ds.child("imagenUrl").value}"

                            val modeloImgSelec = ModeloImagenSeleccionada(
                                id=id, imagenUri = null , imagenUrl = imagenUrl , deInternet = true
                            )
                            imagenSelecArrayList.add(modeloImgSelec)
                        }
                        cargarImagenes()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun calcularPrecioDesc() {
        val precioOriginal = binding.etPrecioP.text.toString()
        val notaDescuento = binding.etNotaDescuentoP.text.toString()
        val porcentaje = binding.etPorcentajeDescuentoP.text.toString()

        if (precioOriginal.isEmpty()){
            Toast.makeText(this, "Ingrese el precio original",Toast.LENGTH_SHORT).show()
        }else if (notaDescuento.isEmpty()){
            Toast.makeText(this, "Ingrese la nota con descuento",Toast.LENGTH_SHORT).show()
        }else if (porcentaje.isEmpty()){
            Toast.makeText(this, "Ingrese el porcentaje del descuento",Toast.LENGTH_SHORT).show()
        }else{
            val precioOriginalDouble = precioOriginal.toDouble()
            val porcentajeDouble = porcentaje.toDouble()
            val descuento = precioOriginalDouble*(porcentajeDouble/100) //Obtenemos el descuento
            val precioDescAplicado = (precioOriginalDouble - descuento) //Precio con descuento
            binding.etPrecioConDescuentoP.text = precioDescAplicado.toInt().toString()
        }
    }

    private var nombreP = ""
    private var descripcionP = ""
    private var categoriaP = ""
    private var precioP = ""
    private var descuentoHab = false
    private var precioDescP = ""
    private var notaDescP = ""
    private var porcentajeDescP = ""
    private fun validarInfo() {
        nombreP = binding.etNombresP.text.toString().trim()
        descripcionP = binding.etDescripcionP.text.toString().trim()
        categoriaP = binding.Categoria.text.toString().trim()
        precioP = binding.etPrecioP.text.toString().trim()
        descuentoHab = binding.descuentoSwitch.isChecked

        if (nombreP.isEmpty()){
            binding.etNombresP.error = "Ingrese nombre"
            binding.etNombresP.requestFocus()
        }
        else if (descripcionP.isEmpty()){
            binding.etDescripcionP.error = "Ingrese descripción"
            binding.etDescripcionP.requestFocus()
        }
        else if (categoriaP.isEmpty()){
            binding.Categoria.error = "Seleccione una categoría"
            binding.Categoria.requestFocus()
        }
        else if (precioP.isEmpty()){
            binding.etPrecioP.error = "Ingrese precio"
            binding.etPrecioP.requestFocus()
        }
        else{
            //descuentoHab = true
            if (descuentoHab){
                notaDescP = binding.etNotaDescuentoP.text.toString().trim()
                porcentajeDescP = binding.etPorcentajeDescuentoP.text.toString().trim()
                precioDescP = binding.etPrecioConDescuentoP.text.toString().trim()

                if (notaDescP.isEmpty()){
                    binding.etNotaDescuentoP.error = "Ingrese una nota"
                    binding.etNotaDescuentoP.requestFocus()
                }else if (porcentajeDescP.isEmpty()){
                    binding.etPorcentajeDescuentoP.error = "Ingrese un porcentaje"
                    binding.etPorcentajeDescuentoP.requestFocus()
                } else if (precioDescP.isEmpty()){
                    binding.etPrecioConDescuentoP.setText("No se estabeleció el precio con descuento")
                }else{
                    if (Edicion){
                        actualizarInfo()
                    }else{
                        if(imagenUri==null){
                            Toast.makeText(this,"Agregue al menos una imagen", Toast.LENGTH_SHORT).show()
                        }else{
                            agregarProducto()
                        }
                    }
                }
            }
            //descuentoHab = false
            else{
                precioDescP = "0"
                notaDescP = ""
                if (Edicion){
                    actualizarInfo()
                }else{
                    if(imagenUri==null){
                        Toast.makeText(this,"Agregue al menos una imagen", Toast.LENGTH_SHORT).show()
                    }else{
                        agregarProducto()
                    }
                }
            }
        }
    }

    private fun actualizarInfo() {
        progressDialog.setMessage("Actualizando producto")
        progressDialog.show()

        val hashMap = HashMap<String, Any>()
        hashMap["nombre"] = "${nombreP}"
        hashMap["descripcion"] = "${descripcionP}"
        hashMap["categoria"] ="${categoriaP}"
        hashMap["precio"] = "${precioP}"
        hashMap["precioDescuento"] = "${precioDescP}"
        hashMap["descuento"] = if (porcentajeDescP.isEmpty()) 0 else porcentajeDescP.toInt()
        hashMap["notaDesc"] = "${notaDescP}"

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                subirImgsStorage(idProducto)
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this,"Falló la actualización debido a ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun agregarProducto() {
        progressDialog.setMessage("Agregando producto")
        progressDialog.show()

        var ref = FirebaseDatabase.getInstance().getReference("Productos")
        val keyId = ref.push().key

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "${keyId}"
        hashMap["nombre"] = "${nombreP}"
        hashMap["descripcion"] = "${descripcionP}"
        hashMap["categoria"] ="${categoriaP}"
        hashMap["precio"] = "${precioP}"
        hashMap["precioDescuento"] = "${precioDescP}"
        hashMap["descuento"] = if (porcentajeDescP.isEmpty()) 0 else porcentajeDescP.toInt()
        hashMap["notaDesc"] = "${notaDescP}"

        ref.child(keyId!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                subirImgsStorage(keyId)
            }
            .addOnFailureListener {e->
                Toast.makeText(this, "${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun subirImgsStorage(keyId: String){
        for (i in imagenSelecArrayList.indices){
            val modeloImagenSel = imagenSelecArrayList[i]

            if (!modeloImagenSel.deInternet){
                val nombreImagen = modeloImagenSel.id
                val rutaImagen = "Productos/$nombreImagen"

                val storageRef = FirebaseStorage.getInstance().getReference(rutaImagen)
                storageRef.putFile(modeloImagenSel.imagenUri!!)
                    .addOnSuccessListener {taskSnapshot->
                        val uriTask = taskSnapshot.storage.downloadUrl
                        while (!uriTask.isSuccessful);
                        val urlImgCargada = uriTask.result

                        if (uriTask.isSuccessful){
                            val hashMap = HashMap<String, Any>()
                            hashMap["id"] = "${modeloImagenSel.id}"
                            hashMap["imagenUrl"] = "${urlImgCargada}"

                            val ref = FirebaseDatabase.getInstance().getReference("Productos")
                            ref.child(keyId).child("Imagenes")
                                .child(nombreImagen)
                                .updateChildren(hashMap)

                        }
                        if (Edicion){
                            progressDialog.dismiss()
                            val intent = Intent(this@AgregarProductoActivity, MainActivityVendedor::class.java)
                            startActivity(intent)
                            Toast.makeText(this,"Se actualizó la información del producto",Toast.LENGTH_SHORT).show()
                            finishAffinity()
                        }else{
                            progressDialog.dismiss()
                            Toast.makeText(this, "Se agregó el producto",Toast.LENGTH_SHORT).show()
                            limpiarCampos()
                        }
                    }
                    .addOnFailureListener {e->
                        progressDialog.dismiss()
                        Toast.makeText(this, "${e.message}",Toast.LENGTH_SHORT).show()
                    }
            }


        }

    }

    private fun limpiarCampos() {
        imagenSelecArrayList.clear()
        adaptadorImagenSel.notifyDataSetChanged()
        binding.etNombresP.setText("")
        binding.etDescripcionP.setText("")
        binding.etPrecioP.setText("")
        binding.Categoria.setText("")
        binding.descuentoSwitch.isChecked = false
        binding.etNotaDescuentoP.setText("")
        binding.etPorcentajeDescuentoP.setText("")
        binding.etPrecioConDescuentoP.setText("")

    }

    private fun cargarCategorias() {
        categoriasArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categorias").orderByChild("categoria")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriasArrayList.clear()
                for (ds in snapshot.children){
                    val modelo = ds.getValue(ModeloCategoria::class.java)
                    categoriasArrayList.add(modelo!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private var idCat = ""
    private var tituloCat = ""
    private fun selecCategorias(){
        val categoriasArray = arrayOfNulls<String>(categoriasArrayList.size)
        for (i in categoriasArray.indices){
            categoriasArray[i] = categoriasArrayList[i].categoria
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccione una categoría")
            .setItems(categoriasArray){dialog, witch->

                idCat = categoriasArrayList[witch].id
                tituloCat = categoriasArrayList[witch].categoria
                binding.Categoria.text = tituloCat
            }
            .show()
    }

    private fun cargarImagenes() {
        adaptadorImagenSel = AdaptadorImagenSeleccionada(this, imagenSelecArrayList, idProducto)
        binding.RVImagenesProducto.adapter = adaptadorImagenSel
    }

    private fun seleccionarImg(){
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent->
                resultadoImg.launch(intent)
            }
    }

    private val resultadoImg =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){resultado->
            if (resultado.resultCode == Activity.RESULT_OK){
                val data = resultado.data
                imagenUri = data!!.data
                val tiempo = "${Constantes().obtenerTiempoD()}"

                val modeloImgSel = ModeloImagenSeleccionada(tiempo, imagenUri, null, false)
                imagenSelecArrayList.add(modeloImgSel)
                cargarImagenes()
            }else{
                Toast.makeText(this,"Acción cancelada",Toast.LENGTH_SHORT).show()
            }
        }
}