package com.example.sahibinden.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.sahibinden.adapter.PopulerUrunler.PopulerUrunlerViewHolder
import com.example.sahibinden.databinding.FeedRowBinding
import com.example.sahibinden.databinding.MainpagePopulerurunRowBinding
import com.example.sahibinden.model.Urun
import com.example.sahibinden.view.FeedFragmentDirections
import com.squareup.picasso.Picasso

class FeedAdapter(private var urunList:ArrayList<Urun>): RecyclerView.Adapter<FeedAdapter.FeedUrunHolder>() {

    class FeedUrunHolder(val binding: FeedRowBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedUrunHolder {
        val binding = FeedRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FeedUrunHolder(binding)
    }

    override fun getItemCount(): Int {
        return urunList.size
    }

    override fun onBindViewHolder(holder: FeedUrunHolder, position: Int) {
        holder.binding.FeedName.text=urunList[position].urunName.toString()
        Picasso.get().load(urunList[position].urunGorselDownloadsLink).into(holder.binding.feedImage)
        holder.itemView.setOnClickListener {
            val action = FeedFragmentDirections.actionFeedFragmentToNewProductFragment("adapter",urunList[position].urunName.toString(),urunList[position].urunSahibiEmail)
            Navigation.findNavController(holder.itemView).navigate(action)
        }

    }
}