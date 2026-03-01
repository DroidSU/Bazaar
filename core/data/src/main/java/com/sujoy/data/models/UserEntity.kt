package com.sujoy.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sujoy.common.ConstantsManager

@Entity(tableName = ConstantsManager.TABLE_USER)
data class UserEntity(
    @PrimaryKey
    var userId: String = "",
    var mobileNumber: String = "",
    var lastSyncTimestamp : Long = 0L,
    var syncState: SyncState = SyncState.PENDING
)