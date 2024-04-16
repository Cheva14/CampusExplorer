package com.cheva.campusexplorer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel: ViewModel() {
    private val _uid: MutableLiveData<String?> = MutableLiveData(null)
    private val auth = Firebase.auth
    val uid: LiveData<String?> get() = _uid
    init {
        _uid.postValue(auth.uid)

    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            auth.signOut()
            _uid.postValue(null)
        }
    }
}