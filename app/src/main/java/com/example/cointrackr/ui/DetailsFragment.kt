package com.example.cointrackr.ui

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.cointrackr.R
import com.example.cointrackr.databinding.FragmentDetailsBinding
import com.example.cointrackr.models.CryptoCurrency
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.security.Key
import kotlin.math.roundToInt

class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private val item:DetailsFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailsBinding.inflate(layoutInflater)
        val data : CryptoCurrency = item.data!!

        binding.backStackButton.setOnClickListener{
            findNavController().popBackStack()
        }

        setUpDetails(data)
        loadChart(data)
        setButtonOnClick(data)
        addToWatchList(data)

        return binding.root
    }

    var watchList : ArrayList<String>? =null
    var watchListIsChecked = false

    private fun addToWatchList(data : CryptoCurrency) {
        readData()
        watchListIsChecked = if(watchList!!.contains(data.symbol)){
            binding.addWatchlistButton.setImageResource(R.drawable.ic_star)
            true
        }else {
            binding.addWatchlistButton.setImageResource(R.drawable.ic_star_outline)
            false
        }
        binding.addWatchlistButton.setOnClickListener{
            watchListIsChecked=
                if(!watchListIsChecked){
                    if(!watchList!!.contains(data.symbol)){
                        watchList!!.add(data.symbol)
                    }
                    storeData()
                    binding.addWatchlistButton.setImageResource(R.drawable.ic_star)
                    true
                }else {
                    binding.addWatchlistButton.setImageResource(R.drawable.ic_star_outline)
                    watchList!!.remove(data.symbol)
                    storeData()
                    false
                }
        }


    }

    private fun storeData(){
        val sharedPreferences = requireContext().getSharedPreferences("watchlist", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(watchList)
        editor.putString("watchlist",json)
        editor.apply()
   }

    private fun readData() {
        val sharedPreferences = requireContext().getSharedPreferences("watchlist", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("watchlist",ArrayList<String>().toString())
        val type = object : TypeToken<ArrayList<String>>(){}.type
        watchList = gson.fromJson(json,type)
    }

    // loads the chart based on different inputs
    private fun setButtonOnClick(data: CryptoCurrency) {
        val oneMonth = binding.button1Month
        val oneWeek = binding.button1Week
        val oneDay = binding.button1Day
        val fourHr = binding.button4Hr
        val oneHr = binding.button1Hr
        val fifteenMin = binding.button15Min
        val clickListener = View.OnClickListener {
            when(it.id){
                fifteenMin.id -> loadChartData(it, "15", data,oneDay,oneMonth,oneWeek,fourHr,oneHr)
                oneHr.id -> loadChartData(it, "1H", data,oneDay,oneMonth,oneWeek,fourHr,fifteenMin)
                fourHr.id -> loadChartData(it, "4H", data,oneDay,oneMonth,oneWeek,fifteenMin,oneHr)
                oneDay.id -> loadChartData(it, "D", data,fifteenMin,oneMonth,oneWeek,fourHr,oneHr)
                oneWeek.id -> loadChartData(it, "W", data,oneDay,oneMonth,fifteenMin,fourHr,oneHr)
                oneMonth.id -> loadChartData(it, "M", data,oneDay,fifteenMin,oneWeek,fourHr,oneHr)
            }
        }
        fifteenMin.setOnClickListener(clickListener)
        oneHr.setOnClickListener(clickListener)
        fourHr.setOnClickListener(clickListener)
        oneDay.setOnClickListener(clickListener)
        oneWeek.setOnClickListener(clickListener)
        oneMonth.setOnClickListener(clickListener)

    }

    private fun loadChartData(
        it: View?,
        s: String,
        data: CryptoCurrency,
        oneDay: AppCompatButton,
        oneMonth: AppCompatButton,
        oneWeek: AppCompatButton,
        fourHr: AppCompatButton,
        oneHr: AppCompatButton
    ) {
        disableButton(oneDay, oneMonth, oneWeek, fourHr, oneHr)
        it?.setBackgroundResource(R.drawable.active_button)
        binding.detaillChartWebView.settings.javaScriptEnabled = true
        binding.detaillChartWebView.setLayerType(View.LAYER_TYPE_SOFTWARE,null)

        binding.detaillChartWebView.loadUrl(
                "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=" + data.symbol
                    .toString() + "USD&interval="+s+"&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=" +
                        "F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=" +
                        "[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT"
        )
    }

    private fun disableButton(oneDay: AppCompatButton, oneMonth: AppCompatButton, oneWeek: AppCompatButton, fourHr: AppCompatButton, oneHr: AppCompatButton) {
        oneDay.background = null
        oneMonth.background = null
        oneWeek.background = null
        fourHr.background = null
        oneHr.background = null

    }

    // loads the chart of the selected Crypto coin
    private fun loadChart(item: CryptoCurrency){
        binding.detaillChartWebView.settings.javaScriptEnabled = true
        binding.detaillChartWebView.setLayerType(View.LAYER_TYPE_SOFTWARE,null)

        binding.detaillChartWebView.loadUrl(
            "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=" + item.symbol
                .toString() + "USD&interval=D&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT"
        )

    }

// load the Crypto currency Image , Price , PercentageChange(net profit or loss)..
    private fun setUpDetails(data: CryptoCurrency) {
        binding.detailSymbolTextView.text = data.symbol

        Glide.with(requireContext()).load(
            "https://s2.coinmarketcap.com/static/img/coins/64x64/"+data.id+".png"
        ).thumbnail(Glide.with(requireContext()).load(R.drawable.spinner))
            .into(binding.detailImageView)

        binding.detailPriceTextView.text = "$${(data.quotes[0].price*10000.0).roundToInt() / 10000.0}"

        val currency_inc = data.quotes!![0].percentChange24h
        if(currency_inc>0){
            context?.resources?.let { binding.detailChangeTextView.setTextColor(it.getColor(R.color.green)) }
            binding.detailChangeImageView.setImageResource(R.drawable.ic_caret_up)
            binding.detailChangeTextView.text ="+${(currency_inc*100.0).roundToInt() / 100.0} %"

        }else{
            context?.resources?.let { binding.detailChangeTextView.setTextColor(it.getColor((R.color.red))) }
            binding.detailChangeImageView.setImageResource(R.drawable.ic_caret_down)
            binding.detailChangeTextView.text ="${(currency_inc*100.0).roundToInt() / 100.0} %"
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("CoinTrackr", "onPause: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d("CoinTrackr", "onResume: ")
    }


}

