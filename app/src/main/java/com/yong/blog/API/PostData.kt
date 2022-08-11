package com.yong.blog.API

data class PostData(
    var postContent: String,
    var postDate: String,
    var postIsPinned: Boolean,
    var postTag: List<String>,
    var postTitle: String,
    var postURL: String
)