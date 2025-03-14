package com.example.tiendavirtualapp_kotlin2.Adaptadores

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tiendavirtualapp_kotlin2.Modelos.ModeloImgSlider
import com.example.tiendavirtualapp_kotlin2.R
import com.example.tiendavirtualapp_kotlin2.databinding.ItemImagenSliderBinding
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView

class AdaptadorImgSlider : RecyclerView.Adapter<AdaptadorImgSlider.HolderImagenSlider>{

    private lateinit var binding : ItemImagenSliderBinding
    private var context : Context
    private var imagenArrayList : ArrayList<ModeloImgSlider>

    constructor(context: Context, imagenArrayList: ArrayList<ModeloImgSlider>) {
        this.context = context
        this.imagenArrayList = imagenArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagenSlider {
        binding = ItemImagenSliderBinding.inflate(LayoutInflater.from(context),parent, false)
        return HolderImagenSlider(binding.root)
    }

    override fun getItemCount(): Int {
        return imagenArrayList.size
    }

    override fun onBindViewHolder(holder: HolderImagenSlider, position: Int) {
        val modeloImagenSlider = imagenArrayList[position]

        val imagenUrl = modeloImagenSlider.imagenUrl
        val imagenContador = "${position+1}/${imagenArrayList.size}" //2/4 3/4
        holder.imagenContadorTv.text = imagenContador

        try {
            Glide.with(context)
                .load(imagenUrl)
                .placeholder(R.drawable.item_img_producto)
                .into(holder.imagenSIV)
        }catch (e:Exception){

        }

        holder.itemView.setOnClickListener {
            zoomImg(imagenUrl)
        }

    }

    inner class HolderImagenSlider(itemView : View) : RecyclerView.ViewHolder(itemView){
        var imagenSIV : ShapeableImageView = binding.imagenSIV
        var imagenContadorTv : TextView = binding.imagenContadorTv
    }

    private fun zoomImg(imagen : String){
        val pv : PhotoView
        val btnCerrar : MaterialButton

        val dialog = Dialog(context)

        dialog.setContentView(R.layout.zoom_imagen)

        pv = dialog.findViewById(R.id.zoomImg)
        btnCerrar = dialog.findViewById(R.id.cerrarZoom)

        try {
            Glide.with(context)
                .load(imagen)
                .placeholder(R.drawable.item_img_producto)
                .into(pv)
        }catch (e:Exception){

        }

        btnCerrar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
    }













}