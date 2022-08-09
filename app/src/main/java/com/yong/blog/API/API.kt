package com.yong.blog.API

import android.util.Log
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query


object API {
    fun getServerPostData(postType: String, postID: String): PostData{
        var postDate = ""
        var postIsPinned = false
        var postTag = List(0){""}
        var postTitle = ""
        var postURL = ""

        val callGetPostList = RetrofitUtil.RetrofitService.getPostData(postType, postID)
        callGetPostList.enqueue(object : Callback<PostDataResponse>{
            override fun onResponse(call: Call<PostDataResponse>, response: Response<PostDataResponse>){
                if(response.isSuccessful()){
                    postDate = response.body()?.RESULT_DATA!!.PostDate
                    postIsPinned = response.body()?.RESULT_DATA!!.PostIsPinned
                    postTag = response.body()?.RESULT_DATA!!.PostTag
                    postTitle = response.body()?.RESULT_DATA!!.PostTitle
                    postURL = response.body()?.RESULT_DATA!!.PostURL
                }
            }

            override fun onFailure(call: Call<PostDataResponse>, t: Throwable) {
                Log.e("API_ERR", t.toString())
            }
        })

        return PostData(postDate, postIsPinned, postTag, postTitle, postURL)
    }

    fun getServerPostList(postType: String): PostList{
        var postCount = 0
        var postList = List(0){PostListItem("", "", false, List(0){""}, "", "")}

        val callGetPostList = RetrofitUtil.RetrofitService.getPostList(postType)
        callGetPostList.enqueue(object : Callback<PostListResponse>{
            override fun onResponse(call: Call<PostListResponse>, response: Response<PostListResponse>){
                if(response.isSuccessful()){
                    postCount = response.body()?.RESULT_DATA!!.PostCount
                    postList = response.body()?.RESULT_DATA!!.PostList
                }
            }

            override fun onFailure(call: Call<PostListResponse>, t: Throwable) {
                Log.e("API_ERR", t.toString())
            }
        })

        return PostList(postCount, postList)
    }

    fun getServerPostImage(postType: String, postID: String, srcID: String): String{
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

    val RetrofitService = initRetrofit()
}

interface RetrofitInterface {
    @POST("/getPostData")
    fun getPostData(@Query("postType") postType: String,
                   @Query("postID") postID: String):
            Call<PostDataResponse>

    @POST("/getPostList")
    fun getPostList(@Query("postType") postType: String):
            Call<PostListResponse>
}

data class PostDataResponse (
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