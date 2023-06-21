package com.example.cointrackr.models

import java.io.Serializable

data class Data(
    val cryptoCurrencyList: List<CryptoCurrency>,
    val totalCount: String
): Serializable {

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String {
        return super.toString()
    }
}