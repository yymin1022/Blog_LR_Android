package com.yong.blog.API

object API {
    fun getServerPostData(postType: String, postID: String): PostData{
        var postDate = "20220101"
        var postIsPinned = false
        var postTag = Array(0){}
        var postTitle = "Test Post Title"
        var postURL = "TEST_POST_URL"

        val curPostData = PostData()
        curPostData.postDate = postDate
        curPostData.postIsPinned = postIsPinned
        curPostData.postTag = postTag
        curPostData.postTitle = postTitle
        curPostData.postURL = postURL

        return curPostData
    }

    fun getServerPostList(postType: String): PostList{
        val curPostList = PostList()
        curPostList.postCount = 1
        curPostList.postList = Array(0){}

        return curPostList
    }

    fun getServerPostImage(postType: String, postID: String, srcID: String): String{
        val curSrcData = "TEST_SRC"

        return curSrcData
    }
}