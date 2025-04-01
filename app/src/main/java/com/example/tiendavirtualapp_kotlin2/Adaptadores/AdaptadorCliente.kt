package com.example.tiendavirtualapp_kotlin2.Adaptadores

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloUsuario
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.databinding.ItemClienteBinding

class AdaptadorCliente : RecyclerView.Adapter<AdaptadorCliente.HolderUsuario> {

    private lateinit var binding : ItemClienteBinding

    private var mContext : Context
    private var usuarioArrayList : ArrayList<ModeloUsuario>

    constructor(mContext: Context, usuarioArrayList: ArrayList<ModeloUsuario>) {
        this.mContext = mContext
        this.usuarioArrayList = usuarioArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderUsuario {
        binding = ItemClienteBinding.inflate(LayoutInflater.from(mContext), parent , false)
        return HolderUsuario(binding.root)
    }

    override fun getItemCount(): Int {
        return usuarioArrayList.size
    }

    override fun onBindViewHolder(holder: HolderUsuario, position: Int) {
        val modeloUsuario = usuarioArrayList[position]

        val imagenU = modeloUsuario.imagen
        val nombresU = modeloUsuario.nombres
        val emailU = modeloUsuario.email
        val dniU = modeloUsuario.dni
        val ubicacionU = modeloUsuario.direccion
        val telefonoU = modeloUsuario.telefono
        val proveedor = modeloUsuario.proveedor

        holder.nombres.text = "${nombresU}"
        holder.email.text = "${emailU}"
        holder.dni.text = "${dniU}"
        holder.ubicacion.text = "${ubicacionU}"
        holder.telefono.text = "Tel.: ${telefonoU}"
        holder.proveedor.text = "Proveedor: ${proveedor}"

        try {
            Glide.with(mContext)
                .load(imagenU)
                .placeholder(R.drawable.img_perfil)
                .into(holder.imagen)
        }catch (e: Exception){

        }

        holder.btnLlamar.setOnClickListener {
            if (telefonoU.isNotEmpty()){
                llamarCliente(telefonoU)
            }else{
                Toast.makeText(mContext, "El cliente no registró su número telefónico", Toast.LENGTH_SHORT).show()
            }
        }

        holder.btnSms.setOnClickListener {
            if (telefonoU.isNotEmpty()){
                smsCliente(telefonoU)
            }else{
                Toast.makeText(mContext, "El cliente no registró su número telefónico", Toast.LENGTH_SHORT).show()
            }

        }
    }

    inner class HolderUsuario(itemView : View) : RecyclerView.ViewHolder(itemView){
        var imagen = binding.imagenC
        var nombres = binding.nombresCPerfil
        var email = binding.emailCPerfil
        var dni = binding.dniCPerfil
        var ubicacion = binding.ubicacion
        var telefono = binding.telefonoCPerfil
        var proveedor = binding.proveedorCPerfil
        var btnLlamar = binding.btnLlamar
        var btnSms = binding.btnSms
    }

    private fun llamarCliente(telefono : String){
        val intent = Intent(Intent.ACTION_CALL)
        intent.setData(Uri.parse("tel:${telefono}"))
        mContext.startActivity(intent)
    }

    private fun smsCliente(telefono : String){
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.setData(Uri.parse("smsto:$telefono"))
        intent.putExtra("sms_body","Estimado(a) cliente , le escribimos de la tienda xyz ...")
        mContext.startActivity(intent)
    }
}