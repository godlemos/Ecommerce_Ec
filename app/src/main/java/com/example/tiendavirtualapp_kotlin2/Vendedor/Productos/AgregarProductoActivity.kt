package com.example.tiendavirtualapp_kotlin2.Vendedor.Productos

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.databinding.ActivityAgregarProductoBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.ImagePickerActivity

class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarProductoBinding
    private var imagenUri : Uri?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imgAgregarProducto.setOnClickListener {
            seleccionarImg()
        }

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
                binding.imgAgregarProducto.setImageURI(imagenUri)
            }else{
                Toast.makeText(this, "Accion cancelada",Toast.LENGTH_SHORT).show()


            }

        }

}