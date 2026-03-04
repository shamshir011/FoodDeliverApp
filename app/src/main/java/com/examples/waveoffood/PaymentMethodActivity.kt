package com.examples.waveoffood

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.waveoffood.databinding.ActivityPaymentMethodBinding
import com.examples.waveoffood.Fragment.ConratsBottomSheetFragment
import com.examples.waveoffood.Model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PaymentMethodActivity : AppCompatActivity() {

    private val binding: ActivityPaymentMethodBinding by lazy{
        ActivityPaymentMethodBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String
    private lateinit var totalAmount: String
    private lateinit var foodItemName: ArrayList<String>
    private lateinit var foodItemPrice: ArrayList<String>
    private lateinit var foodItemImage: ArrayList<String>
    private lateinit var foodItemDescription: ArrayList<String>
    private lateinit var foodItemQuantities: ArrayList<Int>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId: String
    private lateinit var restaurantId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)


        //Initialize Firebase and user details
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference()

        //Set user data
        setUserData()

        //Get user details from firebase
        val intent = intent
        foodItemName = intent.getStringArrayListExtra("FoodItemName") as ArrayList<String>
        foodItemPrice = intent.getStringArrayListExtra("FoodItemPrice") as ArrayList<String>
        foodItemImage = intent.getStringArrayListExtra("FoodItemImage") as ArrayList<String>
        foodItemDescription = intent.getStringArrayListExtra("FoodItemDescription") as ArrayList<String>
        foodItemQuantities = intent.getIntegerArrayListExtra("FoodItemQuantities") as ArrayList<Int>
        restaurantId = intent.getStringExtra("restaurantId") ?: ""

        Log.d("RestaurantId", restaurantId)
        totalAmount = calculateTotalAmount().toString()
//        binding.totalAmount.isEnabled = false  ***********  +"₹"
        binding.totalBill.setText(totalAmount)

        binding.payNowButton.setOnClickListener {

            when {

                binding.radioUPI.isChecked -> {

                    val upiId = binding.editUpi.text.toString().trim()

                    if (upiId.isEmpty()) {
                        binding.editUpi.error = "Enter UPI ID"
                        binding.editUpi.requestFocus()
                        return@setOnClickListener
                    }

                    if (!isValidUpiId(upiId)) {
                        binding.editUpi.error = "Invalid UPI ID"
                        binding.editUpi.requestFocus()
                        return@setOnClickListener
                    }
                    binding.editUpi.error = null   // clear error
                    simulateUPIPayment()
                }
                binding.radioCOD.isChecked -> {
                    placeOrder()
                }
                else -> {
                    Toast.makeText(this, "Please select payment method", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.navigationButton.setOnClickListener{
            finish()
        }
    }

//    private fun placeOrder(){
//        userId = auth.currentUser?.uid ?: ""
//        val time = System.currentTimeMillis()
//
//        val itemPushKey = databaseReference.child("OrderDetails").push().key ?: return
//        val orderDetails = OrderDetails(userId, restaurantId, name, foodItemName, foodItemPrice, foodItemImage, foodItemQuantities, address, totalAmount, phone, time, itemPushKey, false, true)
//        // ✅ 1. Save in global order list
//        databaseReference.child("OrderDetails")
//            .child(itemPushKey)
//            .setValue(orderDetails)
//
//        // ✅ 2. Save in user history
//        databaseReference.child("user")
//            .child(userId)
//            .child("BuyHistory")
//            .child(itemPushKey)
//            .setValue(orderDetails)
//
//        // ✅ 3. MOST IMPORTANT → Save in restaurant node
//        databaseReference.child("restaurantOrders")
//            .child(restaurantId)
//            .child(itemPushKey)
//            .setValue(orderDetails)
//            .addOnSuccessListener {
//
//                removeItemFromCart()
//                val intent = Intent(this, PaymentCompletedActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
//            .addOnFailureListener {
//                Toast.makeText(this, "Order failed to send to restaurant", Toast.LENGTH_SHORT).show()
//            }
//    }



    /*************************************************************************************************************/
    private fun placeOrder() {

        val userId = auth.currentUser?.uid ?: return
        val time = System.currentTimeMillis()

        val orderId = databaseReference.child("OrderDetails").push().key ?: return

        val orderDetails = OrderDetails(
            userId, restaurantId, name, foodItemName,
            foodItemPrice, foodItemImage, foodItemQuantities,
            address, totalAmount, phone, time,
            orderId, false, true, "Pending"
        )

        val updates = hashMapOf<String, Any>(
            "OrderDetails/$orderId" to orderDetails,
            "user/$userId/BuyHistory/$orderId" to orderDetails,
            "restaurantOrders/$restaurantId/$orderId" to orderDetails
        )

        databaseReference.updateChildren(updates)
            .addOnSuccessListener {
                removeItemFromCart()
                startActivity(Intent(this, PaymentCompletedActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Order failed. Try again.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addOrderToHistory(orderDetails: OrderDetails){
        val userId = auth.currentUser?.uid ?: return
        databaseReference.child("user").child(userId).child("BuyHistory")
            .child(orderDetails.itemPushKey!!)
            .setValue(orderDetails).addOnSuccessListener{

            }
    }

    private fun removeItemFromCart(){
        val userId = auth.currentUser?.uid ?: return
        val cartItemsReference = databaseReference.child("user").child(userId).child("CartItems")
        cartItemsReference.removeValue()
    }
    private fun calculateTotalAmount(): Int {

        var totalAmount = 0

        for (i in foodItemPrice.indices) {

            val cleanPrice = foodItemPrice[i]
                .replace("₹", "")   // remove ₹
                .trim()             // remove spaces

            val priceIntValue = cleanPrice.toInt()
            val quantity = foodItemQuantities[i]
            totalAmount += priceIntValue * quantity
        }

        return totalAmount
    }

    private fun setUserData() {

        val user = auth.currentUser ?: return

        databaseReference.child("user").child(user.uid)
            .addListenerForSingleValueEvent(object: ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot){

                    name = snapshot.child("name").getValue(String::class.java) ?: ""
                    address = snapshot.child("address").getValue(String::class.java) ?: ""
                    phone = snapshot.child("phone").getValue(String::class.java) ?: ""

                }
                override fun onCancelled(error: DatabaseError) {}
            }
        )
    }

    private fun simulateUPIPayment() {

        binding.payNowButton.isEnabled = false
        binding.payNowButton.text = "Processing..."

        Handler(Looper.getMainLooper()).postDelayed({

            placeOrder()

        }, 3000)
    }


//    For valid Upi Id
private fun isValidUpiId(upi: String): Boolean {
    val regex = Regex("^[a-zA-Z0-9._-]{2,}@[a-zA-Z]{2,}$")
    return regex.matches(upi)
}
}