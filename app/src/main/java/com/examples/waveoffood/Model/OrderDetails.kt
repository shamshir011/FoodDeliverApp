package com.examples.waveoffood.Model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class OrderDetails(): Serializable{
    var userUid: String? = null
    var userName: String? = null
    var foodNames: MutableList<String>? = null
    var foodImages: MutableList<String>? = null
    var foodPrices: MutableList<String>? = null
    var foodQuantities: MutableList<Int>? = null
    var address: String? = null
    var totalPrice: String? = null
    var phoneNumber: String? = null
    var orderAccepted: Boolean = false
    var paymentReceived: Boolean = false
    var itemPushKey: String? = null
    var currentTime: Long = 0

      constructor(parcel: Parcel) : this(){
          userUid = parcel.readString()
          userName = parcel.readString()
          address = parcel.readString()
          totalPrice = parcel.readString()
          phoneNumber = parcel.readString()
          orderAccepted = parcel.readByte() != 0.toByte()
          paymentReceived = parcel.readByte() != 0.toByte()
          itemPushKey = parcel.readString()
          currentTime = parcel.readLong()
      }
    constructor(
        userId: String,
        name: String,
        foodItemName: ArrayList<String>,
        foodItemPrice: ArrayList<String>,
        foodItemImage: ArrayList<String>,
        foodItemQuantities: ArrayList<Int>,
        address: String,
        totalAmount: String,
        phone: String,
        time: Long,
        itemPushKey: String?,
        b: Boolean,
        b1: Boolean
    ):this(){
        this.userUid = userId
        this.userName = name
        this.foodNames = foodItemName
        this.foodPrices = foodItemPrice
        this.foodImages = foodItemImage
        this.foodQuantities = foodItemQuantities
        this.address = address
        this.totalPrice = totalAmount
        this.phoneNumber = phone
        this.currentTime = time
        this.itemPushKey = itemPushKey
        this.orderAccepted = b                                           //orderAccepted
        this.paymentReceived = b1                                         //paymentReceived
    }

       fun writeToParcel(parcel: Parcel, flags: Int){
          parcel.writeString(userUid)
          parcel.writeString(userName)
          parcel.writeString(address)
          parcel.writeString(totalPrice)
          parcel.writeString(phoneNumber)
          parcel.writeByte(if(orderAccepted) 1 else 0)
          parcel.writeByte(if(orderAccepted) 1 else 0)
          parcel.writeString(itemPushKey)
          parcel.writeLong(currentTime)
      }



       fun describeContents(): Int {
          return 0
      }
    companion object CREATOR: Parcelable.Creator<OrderDetails>{
        override fun createFromParcel(parcel: Parcel): OrderDetails?{
            return OrderDetails(parcel)
        }

        override fun newArray(size: Int): Array<out OrderDetails?>? {
            return arrayOfNulls(size)
        }
    }
}










//    ******************************************************************************************
//    var userUid: String? = null
//    var userName: String? = null
//    var foodNames: ArrayList<String>? = null
//    var foodPrices: ArrayList<String>? = null
//    var foodImages: ArrayList<String>? = null
//    var foodQuantities: ArrayList<Int>? = null   // ✅ Int instead of String
//    var address: String? = null
//    var phoneNumber: String? = null
//    var currentTime: Long = 0
//    var itemPushKey: String? = null
//    var orderAccepted: Boolean = false
//    var paymentReceived: Boolean = false
//
//    // Secondary constructor (matches your call)
//    constructor(
//        userId: String,
//        name: String,
//        foodItemName: ArrayList<String>,
//        foodItemPrice: ArrayList<String>,
//        foodItemImage: ArrayList<String>,
//        foodItemQuantities: ArrayList<Int>,    // ✅ Int list
//        address: String,
//        phone: String,
//        time: Long,
//        itemPushKey: String?,
//        orderAccepted: Boolean,
//        paymentReceived: Boolean
//    ) : this() {
//        this.userUid = userId
//        this.userName = name
//        this.foodNames = foodItemName
//        this.foodPrices = foodItemPrice
//        this.foodImages = foodItemImage
//        this.foodQuantities = foodItemQuantities   // ✅ works now
//        this.address = address
//        this.phoneNumber = phone
//        this.currentTime = time
//        this.itemPushKey = itemPushKey
//        this.orderAccepted = orderAccepted
//        this.paymentReceived = paymentReceived
//    }
//
//    // Parcel constructor
//    constructor(parcel: Parcel) : this() {
//        userUid = parcel.readString()
//        userName = parcel.readString()
//        foodNames = parcel.createStringArrayList()
//        foodPrices = parcel.createStringArrayList()
//        foodImages = parcel.createStringArrayList()
//        foodQuantities =
//            parcel.readArrayList(Int::class.java.classLoader) as ArrayList<Int>?   // ✅ Int list
//        address = parcel.readString()
//        phoneNumber = parcel.readString()
//        currentTime = parcel.readLong()
//        itemPushKey = parcel.readString()
//        orderAccepted = parcel.readByte() != 0.toByte()
//        paymentReceived = parcel.readByte() != 0.toByte()
//    }
//
//    fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeString(userUid)
//        parcel.writeString(userName)
//        parcel.writeStringList(foodNames)
//        parcel.writeStringList(foodPrices)
//        parcel.writeStringList(foodImages)
//        parcel.writeList(foodQuantities)   // ✅ Int list
//        parcel.writeString(address)
//        parcel.writeString(phoneNumber)
//        parcel.writeLong(currentTime)
//        parcel.writeString(itemPushKey)
//        parcel.writeByte(if (orderAccepted) 1 else 0)
//        parcel.writeByte(if (paymentReceived) 1 else 0)
//    }
//
//    fun describeContents(): Int = 0
//
//    companion object CREATOR : Parcelable.Creator<OrderDetails>{
//        override fun createFromParcel(parcel: Parcel): OrderDetails = OrderDetails(parcel)
//        override fun newArray(size: Int): Array<OrderDetails?> = arrayOfNulls(size)
//    }
//}