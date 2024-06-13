package com.tests.jewelry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.tests.jewelry.databinding.ItemsBinding
import com.tests.jewelry.ui.adapter.JewelryAdapter
import com.tests.jewelry.ui.viewmodel.JewelryViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.res.Resources
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.tests.jewelry.ItemsDirections

class Items : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: ItemsBinding? = null
    private val binding get() = _binding!!

    private val jewelryViewModel: JewelryViewModel by viewModels()

    private lateinit var jewelryAdapter: JewelryAdapter

    private var itemType: String = "all"


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


        jewelryAdapter = JewelryAdapter(
            emptyList(),
            onDeleteClick = { item -> deleteItem(item) },
            onEditClick = { item -> editItem(item) },
            onItemClick =  { item -> itemDetails(item) },
            context = requireContext()
        )

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, calculateSpanCount())
            adapter = jewelryAdapter
        }

        arguments?.getString("itemType")?.let { type ->
            itemType = type
            observeItems(itemType)
        }

        val sortOptions = resources.getStringArray(R.array.sort_options)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.sortSpinner.adapter = adapter

        binding.sortSpinner.onItemSelectedListener = this

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> observeItems(itemType) // Default order
            1 -> observeItemsSortedByPrice(true) // Sort by price ascending
            2 -> observeItemsSortedByPrice(false) // Sort by price descending
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing here if nothing is selected
    }

    private fun observeItems(itemType: String){
        if(itemType == "all"){
            jewelryViewModel.items.observe(viewLifecycleOwner, Observer { items ->
                jewelryAdapter.setItems(items)
            })
        } else {
            jewelryViewModel.getJewelryByType(itemType).observe(viewLifecycleOwner, Observer { items ->
                jewelryAdapter.setItems(items)
            })
        }

        val addJewelryBtn = binding.addJewelryButton
        addJewelryBtn.setOnClickListener {
            findNavController().navigate(R.id.action_itemsFragment_to_newItemFragment)
        }
    }

    private fun observeItemsSortedByPrice(ascending: Boolean) {
        if (ascending) {
            jewelryViewModel.getItemsSortedByPriceAsc().observe(viewLifecycleOwner) { items ->
                jewelryAdapter.setItems(items)
            }
        } else {
            jewelryViewModel.getItemsSortedByPriceDesc().observe(viewLifecycleOwner) { items ->
                jewelryAdapter.setItems(items)
            }
        }
    }

    private fun calculateSpanCount(): Int{
        val displayMetrics = Resources.getSystem().displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val itemWidth = 160
        return (dpWidth / itemWidth).toInt().coerceAtLeast(1)
    }
    private fun deleteItem(item: JewelryEntities){
        jewelryViewModel.deleteJewelry(item)
    }

    private fun editItem(item: JewelryEntities) {
        val action = ItemsDirections.actionItemsFragmentToEditItemFragment(item)
        findNavController().navigate(action)
    }

    private fun itemDetails(item: JewelryEntities) {
        val action = ItemsDirections.actionItemsFragmentToItemDetailsFragment(item)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}