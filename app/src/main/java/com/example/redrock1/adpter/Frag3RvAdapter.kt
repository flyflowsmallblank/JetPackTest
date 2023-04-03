package com.example.redrock1.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.redrock1.OnItemClickListener
import com.example.redrock1.R
import com.example.redrock1.pojo.WeChat

class Frag3RvAdapter(var data : ArrayList<WeChat>) : RecyclerView.Adapter<Frag3RvAdapter.InnerHolder>() {
    var mOnItemClickListener : OnItemClickListener? = null  //这里隐含一个set
        set(value) {field = value}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {
        return InnerHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_wechat,parent,false))
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        holder.tvTitle.text = data[position].name.toString()

        val itemView : View = (holder.itemView as LinearLayout).getChildAt(0)

        if(mOnItemClickListener != null){
            itemView.setOnClickListener {
                val position : Int = holder.layoutPosition
                mOnItemClickListener?.onItemClick(holder.itemView,position)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvTitle : TextView = itemView.findViewById(R.id.frag3_rv_tv_title)
    }

}