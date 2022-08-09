package com.yong.blog.API

data class PostList(
    val postCount: Int,
    val postList: List<PostListItem>
)

data class PostListItem(
    val postDate: String,
    val postID: String,
    val postIsPinned: Boolean,
    val postTag: List<String>,
    val postTitle: String,
    val postURL: String
)