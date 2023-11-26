package com.tryamb.healthcare.adapter
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tryamb.healthcare.R


class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imageView: ImageView
    var textName: TextView
    var textRating:TextView
    var textAddress:TextView
    var btnContact:Button

    init {
        imageView = itemView.findViewById(R.id.clinicImageView)
        textName = itemView.findViewById(R.id.tvClinicCenterName)
        textRating=itemView.findViewById(R.id.tvRating)
        textAddress=itemView.findViewById(R.id.tvAddress)
        btnContact=itemView.findViewById(R.id.tvContact)

    }
}
