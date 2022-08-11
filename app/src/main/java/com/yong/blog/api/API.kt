package com.yong.blog.api

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

object API {
    fun getServerPostData(postType: String, postID: String): PostData {
        val result = RetrofitUtil.RetrofitService.getPostData(PostDataRequest(postType, postID)).execute().body()!!
        val postContent = result.RESULT_DATA.PostContent
        val postDate = result.RESULT_DATA.PostDate
        val postIsPinned = result.RESULT_DATA.PostIsPinned
        val postTag = result.RESULT_DATA.PostTag
        val postTitle = result.RESULT_DATA.PostTitle
        val postURL = result.RESULT_DATA.PostURL

        return PostData(postContent, postDate, postIsPinned, postTag, postTitle, postURL)
    }

    fun getServerPostList(postType: String): PostList {
        val result = RetrofitUtil.RetrofitService.getPostList(PostListRequest(postType)).execute().body()!!
        val postCount = result.RESULT_DATA.PostCount
        val postList = result.RESULT_DATA.PostList

        return PostList(postCount, postList)
    }

    fun getServerPostImage(postType: String, postID: String, srcID: String): String {
        val curSrcData = "TEST_SRC"

        return curSrcData
    }
}

object RetrofitUtil {
    private const val BASE_URL = "https://api.dev-lr.com"

    private fun initRetrofit() =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitInterface::class.java)

    val RetrofitService: RetrofitInterface = initRetrofit()
}

interface RetrofitInterface {
    @POST("/getPostData")
    fun getPostData(@Body request: PostDataRequest):
            Call<PostDataResponse>

    @POST("/getPostList")
    fun getPostList(@Body request: PostListRequest):
            Call<PostListResponse>
}

data class PostDataRequest(
    val postType: String,
    val postID: String
)

data class PostDataResponse(
    val RESULT_CODE: Int,
    val RESULT_MSG: String,
    val RESULT_DATA: PostDataResponseData
)

data class PostDataResponseData(
    val PostContent: String,
    val PostDate: String,
    val PostIsPinned: Boolean,
    val PostTag: List<String>,
    val PostTitle: String,
    val PostURL: String
)

data class PostListRequest(
    val postType: String,
)

data class PostListResponse(
    val RESULT_CODE: Int,
    val RESULT_MSG: String,
    val RESULT_DATA: PostListResponseData
)

data class PostListResponseData(
    val PostCount: Int,
    val PostList: List<PostListItem>
)