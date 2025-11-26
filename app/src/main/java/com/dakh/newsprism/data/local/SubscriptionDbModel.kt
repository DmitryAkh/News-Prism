package com.dakh.newsprism.data.local

import androidx.room.Entity

@Entity(tableName = "subscriptions")
data class SubscriptionDbModel(val topic: String)
