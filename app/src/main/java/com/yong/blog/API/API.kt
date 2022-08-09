package com.yong.blog.API

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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
        var postCount = 0
        var postList = Array(0){}

        val curPostList = PostList()
        curPostList.postCount = postCount
        curPostList.postList = postList

        return curPostList
    }

    fun getServerPostImage(postType: String, postID: String, srcID: String): String{
        val curSrcData = "TEST_SRC"

        return curSrcData
    }
}

object RetrofitUtil {
    val BASE_URL = "https://api.dev-lr.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

interface PostDataInterface {
    @POST("/getPostData")
    fun getPostData(@Query("postType") postType: String,
                   @Query("postID") postID: String): Call<PostDataResponse>
}

interface PostListInterface {
    @POST("/getPostList")
    fun getPostList(@Query("postType") postType: String): Call<PostListResponse>
}

data class PostDataResponse(
    val RESULT_CODE: Int,
    val RESULT_MSG: String,
    val RESULT_DATA: PostDataResponseData
)

data class PostDataResponseData (
    val PostContent: String,
    val PostDate: String,
    val PostIsPinned: Boolean,
    val PostTag: List<String>,
    val PostTitle: String,
    val PostURL: String
)

data class PostListResponse (
    val RESULT_CODE: Int,
    val RESULT_MSG: String,
    val RESULT_DATA: PostListResponseData
)

data class PostListResponseData (
    val PostCount: Int,
    val PostList: List<PostListItem>
)

data class PostListItem (
    val PostDate: String,
    val PostID: String,
    val PostIsPinned: Boolean,
    val PostTag: List<String>,
    val PostTitle: String,
    val PostURL: String
)