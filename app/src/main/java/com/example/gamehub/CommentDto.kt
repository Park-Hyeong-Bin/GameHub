package com.example.gamehub

data class CommentDto(
    var uid: String,
    var comment: String,
    var name: String,
    var timestamp: Long,
    var favoriteDto: FavoriteDto?
)
    {
    constructor() : this("","", "", 0, null)
}
