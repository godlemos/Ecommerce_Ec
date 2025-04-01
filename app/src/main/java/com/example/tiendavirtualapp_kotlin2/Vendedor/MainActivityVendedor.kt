package com.example.tiendavirtualapp_kotlin2.Vendedor

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.SeleccionarTipoActivity
import com.example.tiendavirtualapp_kotlin2.Vendedor.Bottom_Nav_Fragments_Vendedor.FragmentMisProductosV
import com.example.tiendavirtualapp_kotlin2.Vendedor.Bottom_Nav_Fragments_Vendedor.FragmentOrdenesV
import com.example.tiendavirtualapp_kotlin2.Vendedor.Nav_Fragments_Vendedor.FragmentCategoriasV
import com.example.tiendavirtualapp_kotlin2.Vendedor.Nav_Fragments_Vendedor.FragmentInicioV
import com.example.tiendavirtualapp_kotlin2.Vendedor.Nav_Fragments_Vendedor.FragmentMiTiendaV
import com.example.tiendavirtualapp_kotlin2.Vendedor.Nav_Fragments_Vendedor.FragmentReseniasV
import com.example.tiendavirtualapp_kotlin2.databinding.ActivityMainVendedorBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import android.app.AlertDialog
import com.example.tiendavirtualapp_kotlin2.Vendedor.Nav_Fragments_Vendedor.FragmentProductosV

class MainActivityVendedor : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainVendedorBinding
    private var firebaseAuth : FirebaseAuth?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración de la toolbar
        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()

        // Configuración del Navigation Drawer
        binding.navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.appBarMain.toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Ajuste de padding para la barra de estado
        ViewCompat.setOnApplyWindowInsetsListener(binding.drawerLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Reemplazar el fragmento de inicio por defecto
        replaceFragment(FragmentInicioV())
        binding.navigationView.setCheckedItem(R.id.op_inicio_v)

    }

    private fun cerrarSesion(){
        firebaseAuth!!.signOut()
        startActivity(Intent(applicationContext, SeleccionarTipoActivity::class.java))
        finish()
        Toast.makeText(applicationContext, "Has cerrado sesion", Toast.LENGTH_SHORT).show()
    }

    private fun comprobarSesion() {
        /*Si el usuario no inicio sesion*/
        if (firebaseAuth!!.currentUser==null){
            startActivity(Intent(applicationContext, SeleccionarTipoActivity::class.java))
            Toast.makeText(applicationContext, "Vendedor no registrado o no logeado", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(applicationContext, "Vendedor en linea", Toast.LENGTH_SHORT).show()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.navFragment, fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.op_inicio_v ->{
                replaceFragment(FragmentInicioV())
            }

            R.id.op_mi_tienda_v ->{
                replaceFragment(FragmentMiTiendaV())
            }

            R.id.op_categorias_v->{
                replaceFragment(FragmentCategoriasV())
            }
            R.id.op_mis_productos_v ->{
                replaceFragment(FragmentProductosV())
            }

            R.id.op_cerrar_sesion_v ->{
                cerrarSesion()
            }
            R.id.op_mis_productos_v ->{
                replaceFragment(FragmentMisProductosV())
            }

            R.id.op_mis_ordenes_v ->{
                replaceFragment(FragmentOrdenesV())
            }

        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // Si estamos en el fragmento de inicio, mostrar diálogo de confirmación
            val currentFragment = supportFragmentManager.findFragmentById(R.id.navFragment)
            if (currentFragment is FragmentInicioV) {
                AlertDialog.Builder(this)
                    .setTitle("Cerrar aplicación")
                    .setMessage("¿Estás seguro que deseas salir de la aplicación?")
                    .setPositiveButton("Sí") { _, _ ->
                        super.onBackPressed()
                    }
                    .setNegativeButton("No", null)
                    .show()
            } else {
                // Si no estamos en el fragmento de inicio, volver a él
                replaceFragment(FragmentInicioV())
                binding.navigationView.setCheckedItem(R.id.op_inicio_v)
            }
        }
    }
}