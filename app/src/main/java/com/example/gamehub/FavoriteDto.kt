package com.example.gamehub

data class FavoriteDto(
    var favorite: MutableMap<String, Boolean> = HashMap()
)