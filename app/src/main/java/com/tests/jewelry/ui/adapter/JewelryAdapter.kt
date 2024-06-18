package com.tests.jewelry.ui.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.content.Context
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tests.jewelry.JewelryEntities
import com.tests.jewelry.R
import com.tests.jewelry.databinding.ItemJewelryBinding

class JewelryAdapter(private var itemList: List<JewelryEntities>,
                     private val onDeleteClick: (JewelryEntities) -> Unit,
                     private val onEditClick: (JewelryEntities) -> Unit,
                     private val onItemClick: (JewelryEntities) -> Unit,
                     private val onSoldItemsChange: (JewelryEntities) -> Unit,
                     private val context: Context
) : RecyclerView.Adapter<JewelryAdapter.JewelryViewHolder>() {

    inner class JewelryViewHolder(private val binding: ItemJewelryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: JewelryEntities) {
            // Displaying selected fields
            binding.itemName.text = item.name
            binding.itemPrice.text = "${item.price}₪"
            binding.soldItemsTextView.text = item.soldItems.toString()

            // Display image
            Glide.with(context)
                .load(item.imageResId)
                .centerCrop()
                .into(binding.itemImage)

            // Listener for several actives (see details, delete, edit, sell item)
            binding.deleteButton.setOnClickListener { showDeleteConfirmationDialog(item) }
            binding.editButton.setOnClickListener { onEditClick(item) }
            binding.plusButton.setOnClickListener {
                item.soldItems++
                binding.soldItemsTextView.text = item.soldItems.toString()
                onSoldItemsChange(item)
                Toast.makeText(context, R.string.item_sold_message, Toast.LENGTH_SHORT).show()
            }
            binding.minusButton.setOnClickListener {
                if(item.soldItems > 0) {
                    item.soldItems--
                    binding.soldItemsTextView.text = item.soldItems.toString()
                    onSoldItemsChange(item)
                    Toast.makeText(context, R.string.item_unsold_message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun showDeleteConfirmationDialog(item: JewelryEntities) { // Dialog before deleting
            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.delete_item_message)
                .setMessage(R.string.are_you_sure_delete_message)
                .setNegativeButton(R.string.cancel_btn) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.delete) { dialog, _ ->
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
        val item = itemList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = itemList.size

    fun setItems(items : List<JewelryEntities>) {
        this.itemList = items
        notifyDataSetChanged()
    }
}