package com.cheva.campusexplorer

data class PlaceDetails(
    val title: String = "",
    val description: String = "",
    @field:JvmField // required when a boolean field begins with “is”
    var isSelected: Boolean = false,
    @field:JvmField // required when a boolean field begins with “is”
    val isFound: Boolean = false,
)
