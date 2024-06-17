package com.tests.jewelry.ui.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tests.jewelry.*
import com.tests.jewelry.data.db.entities.SupplierEntities
import com.tests.jewelry.databinding.ItemSupplierBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SupplierAdapter(private var itemList: List<SupplierEntities>,
                      private val onDeleteClick: (SupplierEntities) -> Unit,
                      private val onItemClick: (SupplierEntities) -> Unit,
                      private val context: Context
) : RecyclerView.Adapter<SupplierAdapter.SupplierViewHolder>() {

    inner class SupplierViewHolder(private val binding: ItemSupplierBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SupplierEntities) {
            binding.itemName.text = item.name
            binding.itemDate.text = parseDate(item.date)

            Glide.with(context)
                .load(item.reception)
                .centerCrop()
                .into(binding.itemImage)

            binding.deleteButton.setOnClickListener { showDeleteConfirmationDialog(item) }
            itemView.setOnClickListener { onItemClick(item) }
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

    private fun parseDate(date: Date): String {
        val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateStr.format(date)
    }
}