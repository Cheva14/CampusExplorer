package com.cheva.campusexplorer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {
    private val _msg:MutableLiveData<String?> = MutableLiveData(null)
    private val _uid:MutableLiveData<String?> = MutableLiveData(null)
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    val msg: LiveData<String?> get() = _msg
    val uid: LiveData<String?> get() = _uid

    init {
        _uid.postValue(auth.uid)
    }

    fun newAccount(email:String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _uid.postValue(it.user?.uid)
                    val userDetails = hashMapOf(
                        "email" to it.user?.email,
                        "Lubbers Stadium" to false,
                        "Recreation Center" to false,
                        "GVSU Laker Store" to false,
                        "Cook Carillon Tower (CCT)" to false,
                        "Mary Idema Pew Library (LIB)" to false,
                        "locations" to arrayListOf<GeoPoint>() // Initialize empty array
                    )
                    db.collection("users")
                        .document(it.user?.uid!!) // Set document ID to user UID
                        .set(userDetails)
                        .addOnSuccessListener {
                            println("User details added successfully to Firestore")
                        }
                        .addOnFailureListener { exception ->
                            println("Error adding user details to Firestore: $exception")
                        }
                }
                .addOnFailureListener {
                    _msg.postValue(it.message)
                }
        }
    }

    fun login(email:String, password:String) {
        viewModelScope.launch(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _uid.postValue(it.user?.uid)
                }
                .addOnFailureListener {
                    _msg.postValue(it.message)
                }
        }
    }
}