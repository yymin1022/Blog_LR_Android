package com.yong.blog.API

class API {
    fun getServerPostData(postType: String, postID: String): PostData{
        val curPostData = PostData()
        curPostData.postDate = "20220101"
        curPostData.postIsPinned = false
        curPostData.postTag = Array(0) {}
        curPostData.postTitle = "Test Post Title"
        curPostData.postURL = "TEST_POST"

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