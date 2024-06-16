package com.tests.jewelry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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

        jewelryViewModel.suppliers.observe(viewLifecycleOwner, Observer { supplier ->
            jewelryViewModel.items.observe(viewLifecycleOwner, Observer { jewelry ->
                calculateAndDisplayAnalytics(supplier, jewelry)
            })
        })

        jewelryViewModel.getBestSeller().observe(viewLifecycleOwner, Observer { bestSeller ->
            binding.bestSellerAllTimeValue.text = bestSeller?.name ?: getString(R.string.no_sales)
        })

        jewelryViewModel.getLastMonthBestSeller().observe(viewLifecycleOwner, Observer { bestSeller ->
            binding.bestSellerLastMonthValue.text = bestSeller?.name ?: getString(R.string.no_sales)
        })
    }


    private fun calculateAndDisplayAnalytics(suppliers: List<SupplierEntities>, jewelries: List<JewelryEntities>) {
        val currentDate = Date()
        val lastMonthDate = Calendar.getInstance().apply {
            add(Calendar.MONTH, -1)
        }.time

        var lastMonthExpenses = 0.0
        var allTimeExpenses =  0.0
        var lastMonthRevenue = 0.0
        var allTimeRevenue = 0.0
        var lastMonthBestSeller: JewelryEntities? = null
        var allTimeBestSeller: JewelryEntities? = null

        for (supplier in suppliers) {
            if(supplier.date.after(lastMonthDate)) {
                lastMonthExpenses += supplier.purchasePrice
            }
            allTimeExpenses += supplier.purchasePrice
        }

        for(item in jewelries) {
            if(Date(item.creationTime).after(lastMonthDate)) {
                lastMonthRevenue += item.price * item.soldItems

            }
            allTimeRevenue += item.price * item.soldItems
        }

        val lastMonthProfit = lastMonthRevenue - lastMonthExpenses
        val allTimeProfit = allTimeRevenue - allTimeExpenses

        binding.supplierExpensesLastMonthValue.text = getString(R.string.amount_format, lastMonthExpenses)
        binding.supplierExpensesAllTimeValue.text = getString(R.string.amount_format, allTimeExpenses)
        binding.jewelriesRevenuesLastMonthValue.text = getString(R.string.amount_format, lastMonthRevenue)
        binding.jewelriesRevenuesAllTimeValue.text = getString(R.string.amount_format, allTimeRevenue)
        binding.totalProfitLastMonthValue.text = getString(R.string.amount_format, lastMonthProfit)
        binding.totalProfitAllTimeValue.text = getString(R.string.amount_format, allTimeProfit)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}