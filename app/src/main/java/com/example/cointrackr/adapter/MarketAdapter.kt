package com.example.cointrackr.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cointrackr.R
import com.example.cointrackr.databinding.CurrencyItemLayoutBinding
import com.example.cointrackr.models.CryptoCurrency
import com.example.cointrackr.ui.HomeFragmentDirections
import com.example.cointrackr.ui.MarketFragmentDirections
import com.example.cointrackr.ui.WatchListFragmentDirections
import kotlin.math.roundToInt

class MarketAdapter(var context: Context, var list: List<CryptoCurrency>, var type: String): RecyclerView.Adapter<MarketAdapter.MarketViewHolder>() {
    inner class MarketViewHolder(view: View): RecyclerView.ViewHolder(view){
        var binding = CurrencyItemLayoutBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {
        return MarketViewHolder(LayoutInflater.from(context).inflate(R.layout.currency_item_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        val item = list[position]
        holder.binding.currencyNameTextView.text = item.name
        holder.binding.currencySymbolTextView.text = item.symbol

        Glide.with(context).load(
            "https://s2.coinmarketcap.com/static/img/coins/64x64/"+item.id+".png"
        ).thumbnail(Glide.with(context).load(R.drawable.spinner))
            .into(holder.binding.currencyImageView)

        Glide.with(context).load(
            "https://s3.coinmarketcap.com/generated/sparklines/web/7d/usd/"+item.id+".png"
        ).thumbnail(Glide.with(context).load(R.drawable.spinner))
            .into(holder.binding.currencyChartImageView)

        holder.binding.currencyPriceTextView.text = "$${(item.quotes[0].price*100.0).roundToInt() / 100.0}"

        val currency_inc = item.quotes!![0].percentChange24h
        if(currency_inc>0){
            holder.binding.currencyChangeTextView.setTextColor(context.resources.getColor(R.color.green))
            holder.binding .currencyChartImageView.setColorFilter(ContextCompat.getColor(context, R.color.green))
            holder.binding.currencyChangeTextView.text ="+${(currency_inc*100.0).roundToInt() / 100.0} %"

        }else{
            holder.binding.currencyChangeTextView.setTextColor(context.resources.getColor((R.color.red)))
            holder.binding .currencyChartImageView.setColorFilter(ContextCompat.getColor(context, R.color.red))
            holder.binding.currencyChangeTextView.text ="${(currency_inc*100.0).roundToInt() / 100.0} %"
        }
        holder.itemView.setOnClickListener{
            if(type=="home"){
                findNavController(it).navigate(
                    HomeFragmentDirections.actionHomeFragmentToDetailsFragment(item)
                )
            }else if(type=="market"){
                findNavController(it).navigate(
                    MarketFragmentDirections.actionMarketFragmentToDetailsFragment(item)
                )
            }else{
                findNavController(it).navigate(
                    WatchListFragmentDirections.actionWatchListFragmentToDetailsFragment(item)
                )

            }

        }
    }

    fun updateList(dataItem : List<CryptoCurrency>){
        list = dataItem
        notifyDataSetChanged()
    }
}