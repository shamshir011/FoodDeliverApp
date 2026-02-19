package com.examples.waveoffood.Model

data class Restaurant(
    var key: String? = null,
    val restaurantImage: String? = null,
    val restaurantName: String? = null,
    val deliveryDuration: String? = null,
    val restaurantDeliveryDuration: String? = null,
    val restaurantOpeningTime: String? = null,
    val restaurantClosingTime: String? = null,
    val restaurantAddress: String? = null,
    val restaurantPhone: String? = null
)