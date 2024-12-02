package com.example.sahibinden.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.sahibinden.R
import com.example.sahibinden.adapter.KatagoriAdapter.KatagoriViewHolder
import com.example.sahibinden.databinding.FragmentNewProductBinding
import com.example.sahibinden.databinding.MainpagePopulerurunRowBinding
import com.example.sahibinden.model.Urun
import com.example.sahibinden.view.MainPageFragmentDirections
import com.squareup.picasso.Picasso

class PopulerUrunler(private var populerUrunList: ArrayList<Urun>):RecyclerView.Adapter<PopulerUrunler.PopulerUrunlerViewHolder>() {

    class PopulerUrunlerViewHolder(val binding: MainpagePopulerurunRowBinding):RecyclerView.ViewHolder(binding.root){
        val urunName : TextView = itemView.findViewById(R.id.UrunName)
        val urunGorsel : ImageView = itemView.findViewById(R.id.urunImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopulerUrunlerViewHolder {
        val binding = MainpagePopulerurunRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PopulerUrunlerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return populerUrunList.size
    }

    override fun onBindViewHolder(holder: PopulerUrunlerViewHolder, position: Int) {
        holder.urunName.text = populerUrunList[position].urunName
        Picasso.get().load(populerUrunList[position].urunGorselDownloadsLink).into(holder.urunGorsel)
        holder.itemView.setOnClickListener{
            val action = MainPageFragmentDirections.actionMainPageFragmentToNewProductFragment("adapter",populerUrunList[position].urunName,populerUrunList[position].urunSahibiEmail)
            Navigation.findNavController(holder.itemView).navigate(action)
        }
    }

}