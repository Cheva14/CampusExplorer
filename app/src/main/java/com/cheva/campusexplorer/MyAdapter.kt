package com.cheva.campusexplorer


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

@Suppress("UNUSED_EXPRESSION")
class MyAdapter(private val places: List<PlaceDetails>, val selectionHandler: (PlaceDetails) -> Unit): RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) { // iOS: “table cell”
        val itemTitleText: TextView
        val itemDescriptionText: TextView
        val itemImage: ImageView
        val itemImagePhoto: ImageView
        val itemBg: ConstraintLayout


        init {
            itemTitleText = view.findViewById(R.id.title_text) // ID of TextView in Step 2a
            itemDescriptionText = view.findViewById(R.id.description_text) // ID of TextView in Step 2a
            itemImage = view.findViewById(R.id.image_view) // ID of TextView in Step 2a
            itemImagePhoto = view.findViewById(R.id.image_view_photo) // ID of TextView in Step 2a
            itemBg = view.findViewById(R.id.background)
        }
    }
    // iOS: tableView(_:rowsInSection:)
    override fun getItemCount(): Int = places.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_list_item, parent, false) // Layout file in Step 2a
        return ViewHolder(v)
    }
    // iOS: tableView(_:cellForRowAt:)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = places[position]
        holder.itemTitleText.text = place.title
        holder.itemDescriptionText.text = place.description
        when (place.title) {
            "Lubbers Stadium" -> holder.itemImagePhoto.setImageResource(R.drawable.stadium)
            "Recreation Center" -> holder.itemImagePhoto.setImageResource(R.drawable.rec)
            "The Marketplace" -> holder.itemImagePhoto.setImageResource(R.drawable.store)
            "Cook Carillon Tower" -> holder.itemImagePhoto.setImageResource(R.drawable.tower)
            "Mary Idema Pew Library" -> holder.itemImagePhoto.setImageResource(R.drawable.lib)
            else -> holder.itemImagePhoto.setImageResource(R.drawable.tower)
        }
        if (place.isSelected) {
            holder.itemDescriptionText.text = place.description
            holder.itemImagePhoto.visibility = View.VISIBLE

        } else {
            holder.itemDescriptionText.text = "Click Here to read more..."
            holder.itemImagePhoto.visibility = View.GONE
        }
        if (place.isFound) {
            holder.itemImage.setImageResource(R.drawable.check_24)
            holder.itemBg.setBackgroundResource(R.color.checkBg)
        } else {
            holder.itemDescriptionText.text = "Find this place on the map to learn more..."
            holder.itemImage.setImageResource(R.drawable.clear_24)
            holder.itemBg.setBackgroundResource(R.color.clearBg)
        }
        val you:PlaceDetails = places[position]
        holder.itemView.setOnClickListener {
            println("CLICKED")
            selectionHandler(you)
            true
        }

    }
}