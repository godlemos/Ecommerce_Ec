package com.example.tiendavirtualapp_kotlin2.Cliente

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.tiendavirtualapp_kotlin2.Cliente.Bottom_Nav_Fragments_Cliente.FragmentMisOrdenesC
import com.example.tiendavirtualapp_kotlin2.Cliente.Bottom_Nav_Fragments_Cliente.FragmentTiendaC
import com.example.tiendavirtualapp_kotlin2.Cliente.Nav_Fragments_Cliente.FragmentInicioC
import com.example.tiendavirtualapp_kotlin2.Cliente.Nav_Fragments_Cliente.FragmentMiPerfilC
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.SeleccionarTipoActivity
import com.example.tiendavirtualapp_kotlin2.databinding.ActivityMainClienteBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivityCliente : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainClienteBinding
    private var firebaseAuth : FirebaseAuth?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        binding.navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer

        )
        
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        
        // Obtener el nombre del usuario y mostrarlo en el TextView de bienvenida
        obtenerNombreUsuario()
        
        // Verificar si se debe cargar un fragmento específico
        val fragmentToLoad = intent.getStringExtra("fragmentToLoad")
        if (fragmentToLoad == "misOrdenes") {
            replaceFragment(FragmentMisOrdenesC())
            // Marcar la opción correspondiente en el menú de navegación
            binding.navigationView.setCheckedItem(R.id.op_mis_ordenes_c)
        } else {
            replaceFragment(FragmentInicioC())
        }
    }

    private fun obtenerNombreUsuario() {
        val headerView = binding.navigationView.getHeaderView(0)
        val tvBienvenida = headerView.findViewById<TextView>(R.id.tvBienvenida)
        
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth?.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    if (nombres != "null") {
                        tvBienvenida.text = "Bienvenido(a) ${nombres}"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar error
                }
            })
    }

    private fun comprobarSesion(){
        if (firebaseAuth!!.currentUser==null){
            startActivity(Intent(this@MainActivityCliente, SeleccionarTipoActivity::class.java))
        finishAffinity()
    }else{
        Toast.makeText(this, "Usuario en Linea", Toast.LENGTH_SHORT).show()

    }

    }

    private fun cerrarSesion(){
        firebaseAuth!!.signOut()
        startActivity(Intent(this@MainActivityCliente, SeleccionarTipoActivity::class.java))
        finishAffinity()
        Toast.makeText(this, "Cerraste sesion", Toast.LENGTH_SHORT).show()

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.navFragment,fragment)
            .commit()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.op_inicio_c->{
                replaceFragment(FragmentInicioC())
            }

            R.id.op_mi_perfil_c->{
                replaceFragment(FragmentMiPerfilC())
            }

            R.id.op_cerrar_sesion_c->{
                cerrarSesion()
            }
            R.id.op_tienda_c->{
                replaceFragment(FragmentTiendaC())
            }
            R.id.op_mis_ordenes_c->{
                replaceFragment(FragmentMisOrdenesC())
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true

    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Salir")
            .setMessage("¿Estás seguro que deseas salir de la aplicación?")
            .setPositiveButton("Sí") { _, _ ->
                super.onBackPressed()
            }
            .setNegativeButton("No", null)
            .show()
    }
}