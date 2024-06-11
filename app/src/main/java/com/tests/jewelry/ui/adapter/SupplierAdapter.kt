package com.tests.jewelry.ui.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tests.jewelry.*
import com.tests.jewelry.data.db.entities.SupplierEntities
import com.tests.jewelry.databinding.ItemSupplierBinding

class SupplierAdapter(private var itemList: List<SupplierEntities>,
                      private val onDeleteClick: (SupplierEntities) -> Unit,
                      private val context: Context
) : RecyclerView.Adapter<SupplierAdapter.SupplierViewHolder>() {

    inner class SupplierViewHolder(private val binding: ItemSupplierBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SupplierEntities) {
            binding.itemName.text = item.name
            binding.itemDescription.text = item.type

            val bitmap = BitmapFactory.decodeFile(item.reception)
            binding.itemImage.setImageBitmap(bitmap)

            binding.deleteButton.setOnClickListener { showDeleteConfirmationDialog(item) }
        }

        private fun showDeleteConfirmationDialog(item: SupplierEntities) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplierAdapter.SupplierViewHolder {
        val binding = ItemSupplierBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SupplierViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SupplierAdapter.SupplierViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount() = itemList.size

    fun setItems(items : List<SupplierEntities>) {
        this.itemList = items
        notifyDataSetChanged()
    }
}