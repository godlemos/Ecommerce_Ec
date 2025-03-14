package com.example.tiendavirtualapp_kotlin2.Calificacion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tiendavirtualapp_kotlin2.Adaptadores.AdaptadorCalificacion
import com.dinocode.tiendavirtualapp_kotlin.Modelos.ModeloCalificacion

import com.example.tiendavirtualapp_kotlin2.databinding.ActivityMostrarCalificacionesBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MostrarCalificacionesActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMostrarCalificacionesBinding
    private var idProducto = ""

    private lateinit var calificacionesArrayList : ArrayList<ModeloCalificacion>
    private lateinit var adaptadorCalificacion : AdaptadorCalificacion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMostrarCalificacionesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idProducto = intent.getStringExtra("idProducto").toString()

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        listarCalificaciones(idProducto)

    }

    private fun listarCalificaciones(idProducto: String) {
        calificacionesArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Productos/$idProducto/Calificaciones")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                calificacionesArrayList.clear()
                for (ds in snapshot.children){
                    val modeloCalificacion = ds.getValue(ModeloCalificacion::class.java)
                    calificacionesArrayList.add(modeloCalificacion!!)
                }

                adaptadorCalificacion = AdaptadorCalificacion(this@MostrarCalificacionesActivity , calificacionesArrayList)
                binding.calificacionesRV.adapter = adaptadorCalificacion

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}