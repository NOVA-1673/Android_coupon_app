package com.ex.nfcoupon.feature.Data;


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object FlagStore {
        private val _ready = MutableStateFlow(false)
        val ready: StateFlow<Boolean> = _ready

        fun setReady(value: Boolean) {
        _ready.value = value
        }

        fun isReady(): Boolean = _ready.value
}