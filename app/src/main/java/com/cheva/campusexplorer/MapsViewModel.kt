package com.cheva.campusexplorer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsViewModel: ViewModel() {

    private val _locations = MutableLiveData<List<GeoPoint>>()
    val locations: LiveData<List<GeoPoint>> get() = _locations

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val currentUserUid = auth.currentUser?.uid

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Fetch current locations array from Firestore
            db.collection("users").document("$currentUserUid")
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val locationsFromFirestore = documentSnapshot.get("locations") as? List<GeoPoint>
                        _locations.postValue(locationsFromFirestore!!)
                    } else {
                        println("User document does not exist in Firestore")
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error fetching user document from Firestore: $exception")
                }
        }

//        _uid.postValue(auth.uid)
    }

    fun updateFirestore(locationName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.collection("users").document("$currentUserUid")
                .update(locationName, true)
                .addOnSuccessListener {
                    println("User details updated successfully to Firestore")
                }
                .addOnFailureListener { exception ->
                    println("Error updating user details to Firestore: $exception")
                }
        }
    }

    fun addLocationToFirestore(lat: Double, lng: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val newGeoPoint = GeoPoint(lat, lng)

            // Fetch current locations array from Firestore
            val userDocRef = db.collection("users").document("$currentUserUid")
            userDocRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val locations = documentSnapshot.get("locations") as? List<GeoPoint>

                        // Check if newGeoPoint already exists in locations
                        val alreadyExists = locations?.any { it.latitude == lat && it.longitude == lng } ?: false

                        if (!alreadyExists) {
                            // Add newGeoPoint to the locations array
                            userDocRef.update("locations", FieldValue.arrayUnion(newGeoPoint))
                                .addOnSuccessListener {
                                    println("User location added successfully to Firestore")
                                }
                                .addOnFailureListener { exception ->
                                    println("Error adding user location to Firestore: $exception")
                                }
                        } else {
                            println("Location already exists in Firestore")
                        }
                    } else {
                        println("User document does not exist in Firestore")
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error fetching user document from Firestore: $exception")
                }
        }
    }

//    fun getLocationsFromFirestore(): List<GeoPoint> {
//        return emptyList()
//    }

}