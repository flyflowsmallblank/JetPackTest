package com.example.redrock1.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.redrock1.R
import com.example.redrock1.pojo.MessageInfo


class RecycleViewAdapter(var data : ArrayList<MessageInfo>) : RecyclerView.Adapter<RecycleViewAdapter.InnerHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {
        return InnerHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycle_view_home,parent,false));
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        holder.tvTitle.text = data[position].title.toString()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvTitle : TextView = itemView.findViewById(R.id.rv_tv_title)
    }
}