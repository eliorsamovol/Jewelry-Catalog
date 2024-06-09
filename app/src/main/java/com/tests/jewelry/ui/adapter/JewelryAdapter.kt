package com.tests.jewelry.ui.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tests.jewelry.JewelryEntities
import com.tests.jewelry.databinding.ItemJewelryBinding

class JewelryAdapter(private var itemList: List<JewelryEntities>, private val onDeleteClick: (JewelryEntities) -> Unit
) : RecyclerView.Adapter<JewelryAdapter.JewelryViewHolder>() {

    inner class JewelryViewHolder(private val binding: ItemJewelryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: JewelryEntities) {
            binding.itemName.text = item.name
            binding.itemDescription.text = item.description

            val bitmap = BitmapFactory.decodeFile(item.imageResId)
            binding.itemImage.setImageBitmap(bitmap)

            binding.deleteButton.setOnClickListener { onDeleteClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JewelryViewHolder {
        val binding = ItemJewelryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JewelryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JewelryViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount() = itemList.size

    fun setItems(items : List<JewelryEntities>) {
        this.itemList = items
        notifyDataSetChanged()
    }
}