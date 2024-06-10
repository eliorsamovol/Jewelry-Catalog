package com.tests.jewelry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tests.jewelry.databinding.ItemsBinding
import com.tests.jewelry.ui.adapter.JewelryAdapter
import com.tests.jewelry.ui.viewmodel.JewelryViewModel

class Items : Fragment() {

    private var _binding: ItemsBinding? = null
    private val binding get() = _binding!!

    private val jewelryViewModel: JewelryViewModel by viewModels()

    private lateinit var jewelryAdapter: JewelryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jewelryAdapter = JewelryAdapter(emptyList(),
            onDeleteClick = { item -> deleteItem(item) },
            onEditClick = { item -> editItem(item) },
            context = requireContext())

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = jewelryAdapter
        }

        val itemType = arguments?.getString("itemType") ?: "all"

        if(itemType == "all"){
            jewelryViewModel.items.observe(viewLifecycleOwner) { items ->
                jewelryAdapter.setItems(items)
            }
        } else {
            jewelryViewModel.getJewelryByType(itemType).observe(viewLifecycleOwner) { items ->
                jewelryAdapter.setItems(items)
            }
        }

        jewelryViewModel.items.observe(viewLifecycleOwner) { items ->
            val filteredItems = when (itemType) {
                "necklace" -> items.filter { it.type == "necklace" }
                "ring" -> items.filter { it.type == "ring" }
                "earring" -> items.filter { it.type == "earring" }
                "bracelet" -> items.filter { it.type == "bracelet" }
                else -> items
            }
            jewelryAdapter.setItems(filteredItems)
        }
    }

    private fun deleteItem(item: JewelryEntities){
        jewelryViewModel.deleteJewelry(item)
    }

    private fun editItem(item: JewelryEntities) {
        jewelryViewModel.updateJewelry(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}