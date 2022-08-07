package com.yong.blog.API

class PostData(postType: String, postID: String) {
    var postDate = ""
    var postIsPinned = false
    var postTag = Array(1, {})
    var postTitle = ""
    var postURL = ""
}