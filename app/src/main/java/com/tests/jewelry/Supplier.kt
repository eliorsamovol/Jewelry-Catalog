package com.tests.jewelry

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.tests.jewelry.data.db.entities.SupplierEntities
import com.tests.jewelry.databinding.SuppliersBinding
import com.tests.jewelry.ui.adapter.SupplierAdapter
import com.tests.jewelry.ui.viewmodel.JewelryViewModel
import java.util.concurrent.TimeUnit
import com.tests.jewelry.ItemsDirections



class Supplier : Fragment() {

    private var _binding: SuppliersBinding? = null
    private val binding get() = _binding!!

    private val supplierViewModel: JewelryViewModel by viewModels()

    private lateinit var supplierAdapter: SupplierAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SuppliersBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Define all actions regarding adapter
        supplierAdapter = SupplierAdapter(emptyList(),
            onDeleteClick = { item -> deleteItem(item) },
            onItemClick =  { item -> itemDetails(item) },
            context = requireContext())

        // Initialize RecyclerView
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, calculateSpanCount())
            adapter = supplierAdapter
        }

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.clicked_button)

        binding.addSupplierButton.setOnClickListener {
            binding.addSupplierButton.startAnimation(animation)
            findNavController().navigate(R.id.action_supplier_to_newSupplier)
        }

        binding.backBtn.setOnClickListener {
            binding.backBtn.startAnimation(animation)
            findNavController().navigate(R.id.action_supplier_to_catalog)
        }

        supplierViewModel.suppliers.observe(viewLifecycleOwner) { supplier ->
            supplierAdapter.setItems(supplier)
        }

    }

    private fun calculateSpanCount(): Int{ // Calculating how many items are fit in the screen
        val displayMetrics = Resources.getSystem().displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val itemWidth = 160
        return (dpWidth / itemWidth).toInt().coerceAtLeast(1)
    }

    private fun deleteItem(item: SupplierEntities){ // Delete supplier from suppliers
        supplierViewModel.deleteSupplier(item)
    }

    private fun itemDetails(item: SupplierEntities) { // Moving to supplierDetails fragment
        val action = SupplierDirections.actionSuppliersFragmentToSupplierDetailsFragment(item)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}