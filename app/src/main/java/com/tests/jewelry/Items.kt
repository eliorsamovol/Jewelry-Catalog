package com.tests.jewelry

import android.content.SharedPreferences
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
import android.widget.Spinner
import com.tests.jewelry.ItemsDirections
import android.content.Context

class Items : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: ItemsBinding? = null
    private val binding get() = _binding!!

    private val jewelryViewModel: JewelryViewModel by viewModels()

    private lateinit var jewelryAdapter: JewelryAdapter

    private var itemType: String = "all"

    private lateinit var sharedPreferences: SharedPreferences
    private val PREFS_NAME = "JewelryPrefs"
    private val KEY_JEWELRY_TYPE = "jewelryType"
    private val KEY_SORT_OPTIONS = "sortOption"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ItemsBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jewelryAdapter = JewelryAdapter(
            emptyList(),
            onDeleteClick = { item -> deleteItem(item) },
            onEditClick = { item -> editItem(item) },
            onItemClick =  { item -> itemDetails(item) },
            onSoldItemsChange = { item -> updateSoldItem(item) },
            context = requireContext()
        )

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, calculateSpanCount())
            adapter = jewelryAdapter
        }

        val typeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.jewelry_types,
            android.R.layout.simple_spinner_item
        )
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.jewelryTypeSpinner.adapter = typeAdapter
        binding.jewelryTypeSpinner.onItemSelectedListener = this

//        arguments?.getString("itemType")?.let { type ->
//            itemType = type
//            observeItems(itemType)
//        }

//        val sortOptions = resources.getStringArray(R.array.sort_options)
//        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.sortSpinner.adapter = adapter

        val sortAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_options,
            android.R.layout.simple_spinner_item
        )
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.sortSpinner.adapter = sortAdapter
        binding.sortSpinner.onItemSelectedListener = this

//        binding.sortSpinner.onItemSelectedListener = this

        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_itemsFragment_to_catalog)
            resetSpinnerSelection()
        }

        binding.addJewelryButton.setOnClickListener {
            findNavController().navigate(R.id.action_itemsFragment_to_newItemFragment)
            resetSpinnerSelection()
        }

        restoreSpinnerSelection()
        observeItems(itemType)

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.jewelryTypeSpinner -> {
                itemType = when (position) {
                    0 -> "all" // Adjust based on your spinner items
                    1 -> getString(R.string.necklaces)
                    2 -> getString(R.string.rings)
                    3 -> getString(R.string.earrings)
                    4 -> getString(R.string.bracelets)
                    else -> "all"
                }
                observeItems(itemType)
                saveSpinnerSelections()
            }

            R.id.sortSpinner -> {
                when (position) {
                    0 -> observeItems(itemType) // Default order
                    1 -> observeItemsSortedByPrice(true) // Sort by price ascending
                    2 -> observeItemsSortedByPrice(false) // Sort by price descending
                    3 -> observeItemsSortedByBestSellers()
                }
                saveSpinnerSelections()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing here if nothing is selected
    }

    private fun applyFilter() {
        when (binding.sortSpinner.selectedItemPosition) {
            0 -> observeItems(itemType) // Default order
            1 -> observeItemsSortedByPrice(true) // Sort by price ascending
            2 -> observeItemsSortedByPrice(false) // Sort by price descending
            3 -> observeItemsSortedByBestSellers()
        }
    }

    private fun observeItemsSortedByBestSellers() {
        jewelryViewModel.getItemsSortedByBestSellers().observe(viewLifecycleOwner) { items ->
            jewelryAdapter.setItems(items)
        }
    }

    private fun observeItems(itemType: String){
        if(itemType == "all"){
            jewelryViewModel.items.observe(viewLifecycleOwner, Observer { items ->
                jewelryAdapter.setItems(items)
            })
        } else {
            val localizedType = when(itemType) {
                getString(R.string.necklaces) -> "necklace"
                getString(R.string.rings) -> "ring"
                getString(R.string.earrings) -> "earring"
                getString(R.string.bracelets) -> "bracelet"
                else -> itemType
            }
            jewelryViewModel.getJewelryByType(localizedType).observe(viewLifecycleOwner, Observer { items ->
                jewelryAdapter.setItems(items)
            })
        }

//        val addJewelryBtn = binding.addJewelryButton
//        addJewelryBtn.setOnClickListener {
//            findNavController().navigate(R.id.action_itemsFragment_to_newItemFragment)
//        }
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

    private fun updateSoldItem(item: JewelryEntities){
        jewelryViewModel.updateSoldItems(item)
    }

    private fun itemDetails(item: JewelryEntities) {
        val action = ItemsDirections.actionItemsFragmentToItemDetailsFragment(item)
        findNavController().navigate(action)
    }

    private fun saveSpinnerSelections() {
        val editor = sharedPreferences.edit()
        editor.putInt(KEY_JEWELRY_TYPE, binding.jewelryTypeSpinner.selectedItemPosition)
        editor.putInt(KEY_SORT_OPTIONS, binding.sortSpinner.selectedItemPosition)
        editor.apply()
    }

    private fun restoreSpinnerSelection() {
        val savedJewelryType = sharedPreferences.getInt(KEY_JEWELRY_TYPE, 0)
        val savedSortOption = sharedPreferences.getInt(KEY_SORT_OPTIONS, 0)
        binding.jewelryTypeSpinner.setSelection(savedJewelryType)
        binding.sortSpinner.setSelection(savedSortOption)
    }

    private fun resetSpinnerSelection() {
        val editor = sharedPreferences.edit()
        editor.putInt(KEY_JEWELRY_TYPE, 0)
        editor.putInt(KEY_SORT_OPTIONS, 0)
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}