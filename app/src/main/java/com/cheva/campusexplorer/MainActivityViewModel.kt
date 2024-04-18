package com.cheva.campusexplorer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel: ViewModel() {
    private val _uid: MutableLiveData<String?> = MutableLiveData(null)
    private val _data = MutableLiveData<MutableList<PlaceDetails>>(mutableListOf())

    val places get(): LiveData<MutableList<PlaceDetails>> = _data
    val uid: LiveData<String?> get() = _uid

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    init {
        _uid.postValue(auth.uid)
    }

    fun setPlaceDetailsArrayList() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserUid = auth.currentUser?.uid

            db.collection("users").document("$currentUserUid")
                .get()
                .addOnSuccessListener { userDataSnapshot ->
                    println("${userDataSnapshot.data}")
                    val stadium = userDataSnapshot.getBoolean("Lubbers Stadium")
                    val recCenter = userDataSnapshot.getBoolean("Recreation Center")
                    val lakerStore = userDataSnapshot.getBoolean("GVSU Laker Store")
                    val cookTower = userDataSnapshot.getBoolean("Cook Carillon Tower (CCT)")
                    val library = userDataSnapshot.getBoolean("Mary Idema Pew Library (LIB)")
                    val list: Array<PlaceDetails> = arrayOf(
                        PlaceDetails("Lubbers Stadium",
                            "The Grand Valley State University Laker football team enjoys the friendly confines of Arend D. Lubbers Stadium, one of the top Division II facilities in the nation.\n" +
                                    "\n" +
                                    "Gameday at Lubbers Stadium brings a buzz of excitement and pride to Laker football.\n" +
                                    "\n" +
                                    "The surroundings of Lubbers Stadium have changed almost yearly and add to the overall festivities that are a part of a Laker football Saturday during the fall.\n" +
                                    "\n" +
                                    "GVSU led the nation in attendance for the seventh consecutive year in 2018, averaging 11,104 fans.\n" +
                                    "\n" +
                                    "In 2017 the Lakers set a single-season, school-record average of 13,342 for five home dates.\n" +
                                    "\n" +
                                    "GVSU set a single-season, school-record total of 100,388 for eight home games in 2016, including a single-game attendance mark with 16,889 versus Ferris State.\n" +
                                    "\n" +
                                    "The most recent improvement to Lubbers Stadium is the addition and renovation of the Jamie Hosford Football Center.\n" +
                                    "\n" +
                                    "- 40x72.5’ 977,408 pixel video scoreboard\n" +
                                    "- 22,000 square feet, including the new Hosford Football Center\n" +
                                    "- Athletic training rooms, team meetings rooms, and champion’s lobby\n" +
                                    "- 120-seat theatre meeting room",
                            isSelected = false, isFound = stadium!!),
                        PlaceDetails("Recreation Center",
                            "Originally built in 1996, the Recreation Center was recently expanded and serves the broad recreational needs of a large and diverse student and faculty population.\n" +
                                    "\n" +
                                    "Conveniently located on the Allendale campus, next to the Fieldhouse, and a short distance from numerous living centers, the Recreation Center boasts 95,000 square feet of total recreation space.\n" +
                                    "\n" +
                                    "- Home to Recreation & Wellness and Athletic & Recreation Facility offices.\n" +
                                    "- Space is used for informal recreation and Movement Science classroom space.\n" +
                                    "- Includes a three-court main gymnasium, five additional courts for basketball, volleyball, and multiple activities, a 1/9 mile three-lane track on the third level, two levels of fitness equipment, weight rooms, and stretching/functional training spaces.\n" +
                                    "- Features an instructional fitness studio equipped with spin bikes.\n" +
                                    "- Recreation equipment and outdoor equipment rentals available.\n" +
                                    "- Includes men’s, women’s, and gender-inclusive locker rooms fitted with day-use lockers and showers.\n" +
                                    "- Space is also used for Intramural Sports, Personal Training, Small Group Training, and fitness classes.",
                            isSelected = false, isFound = recCenter!!),
                        PlaceDetails("The Marketplace",
                        "The Marketplace is connected to P. Douglas Kindschi Hall on the Allendale Campus.\n" +
                                "\n" +
                                "It is home to the Laker Store, Starbucks Coffee, and Bento Sushi.\n" +
                                "\n" +
                                "- The Laker Store sells textbooks, GVSU apparel and gifts, and supplies. It also is an Apple Authorized Campus Store.\n" +
                                "- The Marketplace is a popular space for meeting with friends and socializing between classes or for a meal.",
                        isSelected = false, isFound = lakerStore!!),
                        PlaceDetails("Cook Carillon Tower",
                            "The Cook Carillon Tower was built in 1994 and named after longtime Grand Valley donors Peter and Pat Cook.\n" +
                                    "\n" +
                                    "The tower is the most iconic structure on Grand Valley’s Allendale Campus and likely the most recognized structure at Grand Valley.\n" +
                                    "\n" +
                                    "The Cook Carillon Tower contains 48 bronze bells created in the Netherlands. The size and weight of each bell determines the individual tones.\n" +
                                    "\n" +
                                    "The bells range from 7.5 inches to more than 51 inches and weigh from 14 to nearly 3,000 pounds.\n" +
                                    "\n" +
                                    "- The carillon stands 100 feet from the tip of the spire to the ground\n" +
                                    "- Home to an annual weekly summer concert series.\n" +
                                    "- The carillon chimes every 15 minutes throughout the day.",
                            isSelected = false, isFound = cookTower!!),
                        PlaceDetails("Mary Idema Pew Library",
                            "The Mary Idema Pew Library Learning and Information Commons is the intellectual hub of the Allendale Campus.\n" +
                                    "\n" +
                                    "This state-of-the-art building is a prototype for 21st century learning, with an innovative and student-focused design.\n" +
                                    "\n" +
                                    "Students enjoy a range of learning environments, from noisy to quiet, in which they can work on an assignment alone or in a group.\n" +
                                    "\n" +
                                    "It is truly the academic center of campus.\n" +
                                    "\n" +
                                    "- The Atomic Object Technology Showcase gives students a hands-on chance to learn and play with the latest technologies.\n" +
                                    "- The Knowledge Market has highly trained students who work as peer consultants one-on-one or with small groups to help students develop research strategies, writing skills, and polish presentations.\n" +
                                    "- The IT Help Desk loans laptops and iPads, as well as provides hardware and software support for current GVSU students, faculty, and staff.\n" +
                                    "- LEED Platinum certified, the highest LEED certification.",
                            isSelected = false, isFound = library!!),
                    )
                    _data.value?.clear() // Clear existing data
                    _data.value?.addAll(list) // Add new data from the provided list
                    val currentList = _data.value!! // Return if data is null

                    _data.postValue(currentList)

                }
                .addOnFailureListener { exception ->
                    println("Error fetching username: $exception")
                }
        }

    }

    fun showDesc(place: PlaceDetails) {
        val currentList = _data.value ?: return // Return if data is null


        for (item in currentList) {
            if (place.isFound) {
                if (item.title == place.title) { // Find the guess in the list
                    item.isSelected = !item.isSelected
                } else {
                    item.isSelected = false
                }
            }
        }

        _data.postValue(currentList)
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            auth.signOut()
            _uid.postValue(null)
        }
    }
}