package com.yong.blog.API

class API {
    fun getServerPostData(postType: String, postID: String): PostData{
        val curPostData = PostData(postType, postID)
        curPostData.postDate = "20220101"
        curPostData.postIsPinned = false
        curPostData.postTag = Array(1) { "Test Tag" }
        curPostData.postTitle = "Test Post Title"
        curPostData.postURL = "TEST_POST"

        return curPostData
    }

    fun getServerPostList(postType: String){

    }

    fun getServerPostImage(postType: String, postID: String, srcID: String){

    }
}