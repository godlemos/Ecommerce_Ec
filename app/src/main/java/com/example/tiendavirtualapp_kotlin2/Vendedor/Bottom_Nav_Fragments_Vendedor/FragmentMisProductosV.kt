package com.example.tiendavirtualapp_kotlin2.Vendedor.Bottom_Nav_Fragments_Vendedor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tiendavirtualapp_kotlin2.R


class FragmentMisProductosV : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mis_productos_v, container, false)
    }


}