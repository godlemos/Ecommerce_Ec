package com.example.tiendavirtualapp_kotlin2.Vendedor.Nav_Fragments_Vendedor

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tiendavirtualapp_kotlin2.Adaptadores.AdaptadorOrdenCompraV
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloOrdenCompra
import com.example.tiendavirtualapp_kotlin2.databinding.FragmentOrdenesVBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentOrdenesV : Fragment() {

    private lateinit var binding: FragmentOrdenesVBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var ordenesArrayList: ArrayList<ModeloOrdenCompra>
    private lateinit var adaptadorOrdenCompra: AdaptadorOrdenCompraV

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdenesVBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        // Configurar ProgressDialog
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Por favor espere")
        progressDialog.setCanceledOnTouchOutside(false)

        // Configurar RecyclerView
        binding.ordenesRv.layoutManager = LinearLayoutManager(requireContext())

        // Inicializar lista y adaptador
        ordenesArrayList = ArrayList()
        adaptadorOrdenCompra = AdaptadorOrdenCompraV(requireContext(), ordenesArrayList)
        binding.ordenesRv.adapter = adaptadorOrdenCompra

        // Cargar órdenes
        cargarOrdenes()
    }

    private fun cargarOrdenes() {
        // Mostrar progreso
        progressDialog.setMessage("Cargando órdenes...")
        progressDialog.show()

        // Limpiar lista
        ordenesArrayList.clear()

        // Referencia a todas las órdenes
        val refOrdenes = FirebaseDatabase.getInstance().getReference("Ordenes")
        refOrdenes.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Limpiar lista
                ordenesArrayList.clear()
                
                // Si no hay órdenes
                if (!snapshot.exists()) {
                    progressDialog.dismiss()
                    // Mostrar mensaje de no órdenes (usando TextView creado dinámicamente)
                    mostrarMensajeNoOrdenes(true)
                    return
                }
                
                // Recorrer todas las órdenes
                for (ds in snapshot.children) {
                    try {
                        val ordenId = ds.key ?: continue
                        val modeloOrden = ds.getValue(ModeloOrdenCompra::class.java)
                        
                        if (modeloOrden != null) {
                            // Asegurarse de que el ID de la orden esté establecido
                            modeloOrden.idOrden = ordenId
                            
                            // Agregar a la lista
                            ordenesArrayList.add(modeloOrden)
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                
                // Actualizar adaptador
                adaptadorOrdenCompra.notifyDataSetChanged()
                
                // Ocultar progreso
                progressDialog.dismiss()
                
                // Mostrar mensaje si no hay órdenes
                mostrarMensajeNoOrdenes(ordenesArrayList.isEmpty())
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    
    private fun mostrarMensajeNoOrdenes(mostrar: Boolean) {
        if (mostrar) {
            // Si no hay órdenes, mostrar un mensaje
            if (binding.root.findViewById<View>(android.R.id.empty) == null) {
                val emptyText = android.widget.TextView(requireContext())
                emptyText.id = android.R.id.empty
                emptyText.text = "No hay órdenes disponibles"
                emptyText.textSize = 16f
                val params = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                emptyText.layoutParams = params
                (binding.root as ViewGroup).addView(emptyText)
                emptyText.visibility = View.VISIBLE
                
                // Centrar el mensaje
                emptyText.post {
                    val parentWidth = binding.root.width
                    val parentHeight = binding.root.height
                    emptyText.x = (parentWidth - emptyText.width) / 2f
                    emptyText.y = (parentHeight - emptyText.height) / 2f
                }
            } else {
                binding.root.findViewById<View>(android.R.id.empty).visibility = View.VISIBLE
            }
        } else {
            // Si hay órdenes, ocultar el mensaje
            binding.root.findViewById<View>(android.R.id.empty)?.visibility = View.GONE
        }
    }
} 