package com.example.sahibinden.adapter

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.sahibinden.R
import com.example.sahibinden.view.MainPageFragmentDirections

class KatagoriAdapter(private val katagoriList:List<String>):RecyclerView.Adapter<KatagoriAdapter.KatagoriViewHolder>() {

    class KatagoriViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
       val katagoriName : TextView = itemView.findViewById(R.id.KatagoriName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KatagoriViewHolder {
         val view = LayoutInflater.from(parent.context).inflate(R.layout.mainpage_katagori_row,parent,false)
        return KatagoriViewHolder(view)
    }

    override fun getItemCount(): Int {
        return katagoriList.size
    }

    override fun onBindViewHolder(holder: KatagoriViewHolder, position: Int) {

        holder.katagoriName.text = katagoriList[position].toString()
        holder.itemView.setOnClickListener {
            val action = MainPageFragmentDirections.actionMainPageFragmentToFeedFragment(katagoriList[position].toString())
            Navigation.findNavController(holder.itemView).navigate(action)
        }
    }
}