package com.bazaar.dao

import androidx.room.TypeConverter
import com.bazaar.models.SaleItemModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromItemList(value: List<SaleItemModel>): String {
        val gson = Gson()
        val type = object : TypeToken<List<SaleItemModel>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toItemList(value: String): List<SaleItemModel> {
        val gson = Gson()
        val type = object : TypeToken<List<SaleItemModel>>() {}.type
        return gson.fromJson(value, type)
    }
}