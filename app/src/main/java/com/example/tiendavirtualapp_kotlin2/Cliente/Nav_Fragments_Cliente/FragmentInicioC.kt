package com.example.tiendavirtualapp_kotlin2.Cliente.Nav_Fragments_Cliente

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tiendavirtualapp_kotlin2.Cliente.Bottom_Nav_Fragments_Cliente.FragmentMisOrdenesC
import com.example.tiendavirtualapp_kotlin2.Cliente.Bottom_Nav_Fragments_Cliente.FragmentTiendaC
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.databinding.FragmentInicioCBinding


class FragmentInicioC : Fragment() {

    private lateinit var binding: FragmentInicioCBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentInicioCBinding.inflate(inflater, container, false)

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.op_tienda_c->{
                    replaceFragment(FragmentTiendaC())

                }
                R.id.op_mis_ordenes_c->{
                    replaceFragment(FragmentMisOrdenesC())

                }
            }
            true

        }

        replaceFragment(FragmentTiendaC())
        binding.bottomNavigation.selectedItemId = R.id.op_tienda_c

        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.bottomFragment,fragment)
            .commit()

    }



}