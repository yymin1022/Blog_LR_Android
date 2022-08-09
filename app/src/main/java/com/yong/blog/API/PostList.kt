package com.yong.blog.API

data class PostList(
    var postCount: Int,
    var postList: List<PostListItem>
)

data class PostListItem(
    var postDate: String,
    var postID: String,
    var postIsPinned: Boolean,
    var postTag: List<String>,
    var postTitle: String,
    var postURL: String
)