package com.sujoy.data.database.dao

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sujoy.data.models.SaleItemEntity
import com.sujoy.data.models.SyncState

class Converters {
    @TypeConverter
    fun fromItemList(value: List<SaleItemEntity>): String {
        val gson = Gson()
        val type = object : TypeToken<List<SaleItemEntity>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toItemList(value: String): List<SaleItemEntity> {
        val gson = Gson()
        val type = object : TypeToken<List<SaleItemEntity>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromSyncState(value : SyncState) : String {
        return value.name
    }

    @TypeConverter
    fun toSyncState(value : String) : SyncState {
        return SyncState.valueOf(value)
    }
}
