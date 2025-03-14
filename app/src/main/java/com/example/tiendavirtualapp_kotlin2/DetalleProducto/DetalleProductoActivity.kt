package com.example.tiendavirtualapp_kotlin2.DetalleProducto

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.tiendavirtualapp_kotlin2.Adaptadores.AdaptadorImgSlider
import com.example.tiendavirtualapp_kotlin2.Calificacion.CalificarProductoActivity
import com.example.tiendavirtualapp_kotlin2.Calificacion.MostrarCalificacionesActivity
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloImgSlider
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloProducto
import com.example.tiendavirtualapp_kotlin2.databinding.ActivityDetalleProductoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetalleProductoActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetalleProductoBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var idProducto = ""

    private lateinit var imagenSlider : ArrayList<ModeloImgSlider>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        //Obtenemos el id del producto enviado desde el adaptador
        idProducto = intent.getStringExtra("idProducto").toString()

        cargarImagenesProd()
        cargarInfoProducto()

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.tvDejarCalificacion.setOnClickListener {
            val intent = Intent(this , CalificarProductoActivity::class.java)
            intent.putExtra("idProducto", idProducto)
            startActivity(intent)
        }

        binding.tvPromCal.setOnClickListener {
            val intent = Intent(this , MostrarCalificacionesActivity::class.java)
            intent.putExtra("idProducto", idProducto)
            startActivity(intent)
        }

        calcularPromedioCal(idProducto)

    }

    private fun calcularPromedioCal(idProducto: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Productos/$idProducto/Calificaciones")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var sumaCalificaciones = 0
                var totalCalificaciones = 0

                for (calificacionSn in snapshot.children){

                    val calificacion = calificacionSn.child("calificacion").getValue(Int::class.java)

                    if (calificacion != null){
                        sumaCalificaciones += calificacion
                        totalCalificaciones++
                    }

                }

                if (totalCalificaciones > 0){
                    val promedio = sumaCalificaciones.toDouble() / totalCalificaciones //10 / 2 = 5

                    binding.tvPromCal.text = promedio.toString().plus("/5")
                    binding.tvTotalCal.text = ("(${totalCalificaciones})")
                    binding.ratingBar.rating = promedio.toFloat()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })










    }

    private fun cargarInfoProducto() {
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val modeloProducto = snapshot.getValue(ModeloProducto::class.java)

                    val nombre = modeloProducto?.nombre
                    val descripcion = modeloProducto?.descripcion
                    val precio = modeloProducto?.precio
                    val precioDesc = modeloProducto?.precioDescuento
                    val descuentoVal = modeloProducto?.descuento

                    binding.nombrePD.text = nombre
                    binding.descripcionPD.text = descripcion
                    binding.precioPD.text = precio.plus(" USD")

                    if (!precioDesc.equals("") && descuentoVal != null && descuentoVal > 0){
                        /*Producto con descuento*/
                        binding.precioDescPD.text = precioDesc.plus(" USD")
                        binding.notaDescPD.text = "${descuentoVal}% OFF"

                        binding.precioPD.paintFlags = binding.precioPD.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    }else{
                        binding.precioDescPD.visibility = View.GONE
                        binding.notaDescPD.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun cargarImagenesProd() {
        imagenSlider = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto).child("Imagenes")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    imagenSlider.clear()
                    for (ds in snapshot.children){
                        try {
                            val modeloImgSlider = ds.getValue(ModeloImgSlider::class.java)
                            imagenSlider.add(modeloImgSlider!!)
                        }catch (e:Exception){

                        }
                    }
                    val adaptadorImgSlider = AdaptadorImgSlider(this@DetalleProductoActivity, imagenSlider)
                    binding.imagenVP.adapter = adaptadorImgSlider
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}