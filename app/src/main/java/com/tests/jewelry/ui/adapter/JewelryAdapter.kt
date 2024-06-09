package com.tests.jewelry.ui.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tests.jewelry.JewelryEntities
import com.tests.jewelry.databinding.ItemJewelryBinding

class JewelryAdapter(private var itemList: List<JewelryEntities>,
                     private val onDeleteClick: (JewelryEntities) -> Unit,
                     private val context: Context
) : RecyclerView.Adapter<JewelryAdapter.JewelryViewHolder>() {

    inner class JewelryViewHolder(private val binding: ItemJewelryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: JewelryEntities) {
            binding.itemName.text = item.name
            binding.itemDescription.text = item.description

            val bitmap = BitmapFactory.decodeFile(item.imageResId)
            binding.itemImage.setImageBitmap(bitmap)

            binding.deleteButton.setOnClickListener { showDeleteConfirmationDialog(item) }
        }

        private fun showDeleteConfirmationDialog(item: JewelryEntities) {
            MaterialAlertDialogBuilder(context)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Delete") { dialog, _ ->
                    onDeleteClick(item)
                    dialog.dismiss()
                }
                .show()
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