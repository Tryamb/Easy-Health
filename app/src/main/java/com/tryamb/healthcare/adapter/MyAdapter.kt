package com.tryamb.healthcare.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tryamb.healthcare.R
import com.tryamb.healthcare.models.Clinic


class MyAdapter(var context: Context, items: List<Clinic>) :
    RecyclerView.Adapter<MyViewHolder>() {
    var items: List<Clinic>
    init {
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_clinic_row, parent, false)
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.textName.text = items[position].name
        holder.textAddress.text = items[position].address
        holder.textRating.text= "⭐ ${items[position].rating.toString()}"

        val mobNo=items[position].contact

        Picasso.get().load(items[position].image)
            .into(holder.imageView)

        holder.btnContact.setOnClickListener { v ->
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:$mobNo")
            v.context.startActivity(dialIntent)
        }



    }


    override fun getItemCount(): Int {
        return items.size
    }


}

					
					
					