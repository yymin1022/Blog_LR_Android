package com.yong.blog.API

class PostList {
    var postCount = 0
    var postList = List(0){PostListItem("", "", false, List(0){""}, "","")}
}

data class PostListItem(
    val postDate: String,
    val postID: String,
    val postIsPinned: Boolean,
    val postTag: List<String>,
    val postTitle: String,
    val postURL: String
)