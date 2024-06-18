package com.tests.jewelry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.tests.jewelry.data.db.entities.SupplierEntities
import com.tests.jewelry.databinding.BusinessAnalyticsBinding
import com.tests.jewelry.ui.viewmodel.JewelryViewModel
import java.text.SimpleDateFormat
import java.util.*
class BusinessAnalytics : Fragment() {

    private var _binding: BusinessAnalyticsBinding? = null
    private val binding get() = _binding!!

    private val jewelryViewModel: JewelryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BusinessAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Animation
        val scaleAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.text_scale_anim)
        binding.totalProfitValue.startAnimation(scaleAnim)

        jewelryViewModel.suppliers.observe(viewLifecycleOwner, Observer { supplier ->
            jewelryViewModel.items.observe(viewLifecycleOwner, Observer { jewelry ->
                calculateAndDisplayAnalytics(supplier, jewelry)
            })
        })

        jewelryViewModel.getBestSellerByQuantity().observe(viewLifecycleOwner, Observer { bestSeller ->
            binding.bestSellerQuantityValue.text = bestSeller?.name ?: getString(R.string.no_sales)
        })

        jewelryViewModel.getBestSellerByRevenue().observe(viewLifecycleOwner, Observer { bestSeller ->
            binding.bestSellerRevenueValue.text = bestSeller?.name ?: getString(R.string.no_sales)
        })

        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_businessAnalyticsFragment_to_catalog)
        }
    }


    private fun calculateAndDisplayAnalytics(suppliers: List<SupplierEntities>, jewelries: List<JewelryEntities>) {

        var totalExpenses =  0.0
        var totalRevenue = 0.0

        // Calculate total expenses
        suppliers.forEach { supplier ->
            totalExpenses += supplier.purchasePrice
        }

        // Calculate total revenues
        jewelries.forEach { jewelry ->
            totalRevenue += jewelry.price * jewelry.soldItems
        }

        val totalProfit = totalRevenue - totalExpenses

        binding.supplierExpensesValue.text = getString(R.string.amount_format, totalExpenses)
        binding.jewelriesRevenuesValue.text = getString(R.string.amount_format, totalRevenue)
        binding.totalProfitValue.text = getString(R.string.amount_format, totalProfit)

        // In case of positive profit, will displayed in green, and in case of negative in red.
        val profitColor = if(totalProfit >= 0) {
            ContextCompat.getColor(requireContext(), R.color.green)
        } else {
            ContextCompat.getColor(requireContext(), R.color.red)
        }
        binding.totalProfitValue.setTextColor(profitColor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}