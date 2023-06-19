package com.example.cointrackr.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.manager.LifecycleListener
import com.example.cointrackr.R
import com.example.cointrackr.adapter.TopGainLossPageAdapter
import com.example.cointrackr.adapter.TopMarketAdapter
import com.example.cointrackr.api.ApiInterface
import com.example.cointrackr.api.ApiUtilities
import com.example.cointrackr.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        getTopCurrencyList()
        setTabLayout()
        return binding.root
    }

    private fun setTabLayout(){
        val adapter = TopGainLossPageAdapter(this)
        binding.contentViewPager.adapter = adapter

        binding.contentViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(position == 0){
                    binding.topGainIndicator.visibility = VISIBLE
                    binding.topLoseIndicator.visibility = GONE
                }else {
                    binding.topLoseIndicator.visibility = VISIBLE
                    binding.topGainIndicator.visibility = GONE
                }
            }
        })
        TabLayoutMediator(binding.tabLayout, binding.contentViewPager){
            tab, position ->
            var title  = if(position == 0){
                "Top Gainers"
            }else{
                "Top Losers"
            }
            tab.text = title

        }.attach()
    }


    private fun getTopCurrencyList() {
        lifecycleScope.launch (Dispatchers.IO){
            val res = ApiUtilities.getInstance().create(ApiInterface::class.java).getMarketData()

            withContext(Dispatchers.Main){
                binding.topCurrencyRecyclerView.adapter = TopMarketAdapter(requireContext(), res.body()!!.data.cryptoCurrencyList)
            }
//            Log.d("CRYPTO", "getTopCurrencyList: ${res.body()!!.data.cryptoCurrencyList}")
        }
    }


}