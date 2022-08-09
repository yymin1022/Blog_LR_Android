package com.yong.blog.API

data class PostData(
    val postDate: String,
    val postIsPinned: Boolean,
    val postTag: List<String>,
    val postTitle: String,
    val postURL: String
)