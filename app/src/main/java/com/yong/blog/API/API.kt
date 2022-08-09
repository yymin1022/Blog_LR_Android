package com.yong.blog.API

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

object API {
    fun getServerPostData(postType: String, postID: String): PostData{
        val resultPostData = PostData("", false, List(0){""}, "", "")

        val callGetPostList = RetrofitUtil.RetrofitService.getPostData(PostDataRequest(postType, postID))
        callGetPostList.enqueue(object : Callback<PostDataResponse>{
            override fun onResponse(call: Call<PostDataResponse>, response: Response<PostDataResponse>){
                if(response.isSuccessful()){
                    resultPostData.postDate = response.body()?.RESULT_DATA!!.PostDate
                    resultPostData.postIsPinned = response.body()?.RESULT_DATA!!.PostIsPinned
                    resultPostData.postTag = response.body()?.RESULT_DATA!!.PostTag
                    resultPostData.postTitle = response.body()?.RESULT_DATA!!.PostTitle
                    resultPostData.postURL = response.body()?.RESULT_DATA!!.PostURL

                    Log.i("API RESULT", "PostTitle : ${resultPostData.postTitle}")
                    Log.i("API RESULT", "PostUTL : ${resultPostData.postURL}")
                }
            }

            override fun onFailure(call: Call<PostDataResponse>, t: Throwable) {
                Log.e("API_ERR", t.toString())
            }
        })

        return resultPostData
    }

    fun getServerPostList(postType: String): PostList{
        var resultPostList = PostList(0, List(0){PostListItem("", "", false, List(0){""}, "", "")})

        val callGetPostList = RetrofitUtil.RetrofitService.getPostList(PostListRequest(postType))
        callGetPostList.enqueue(object : Callback<PostListResponse>{
            override fun onResponse(call: Call<PostListResponse>, response: Response<PostListResponse>){
                if(response.isSuccessful()){
                    resultPostList.postCount = response.body()?.RESULT_DATA!!.PostCount
                    resultPostList.postList = response.body()?.RESULT_DATA!!.PostList

                    Log.i("API RESULT", "PostCount : ${resultPostList.postCount}")
                    Log.i("API RESULT", "PostList : ${resultPostList.postList}")
                }
            }

            override fun onFailure(call: Call<PostListResponse>, t: Throwable) {
                Log.e("API_ERR", t.toString())
            }
        })

        return resultPostList
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
    fun getPostData(@Body request: PostDataRequest):
            Call<PostDataResponse>

    @POST("/getPostList")
    fun getPostList(@Body request: PostListRequest):
            Call<PostListResponse>
}

data class PostDataRequest (
    val postType: String,
    val postID: String
)

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

data class PostListRequest (
    val postType: String,
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