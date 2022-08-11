package com.yong.blog

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import com.yong.blog.API.API
import com.yong.blog.API.PostData
import com.yong.blog.API.PostList
import com.yong.blog.ui.theme.Blog_LR_AndroidTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Arrays.toString
import java.util.Objects.toString

class PostViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val postID = intent.getStringExtra("postID").toString()
        val postType = intent.getStringExtra("postType").toString()

        setContent {
            Blog_LR_AndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    PostViewUI(postID, postType)
                }
            }
        }
    }
}

@Composable
fun PostViewUI(postID: String, postType: String){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("1인개발자 LR의 IT블로그") }
            )
        }
    ) {
        Column {
            PostViewContainer(postID, postType)
        }
    }
}

@Composable
fun PostViewContainer(postID: String, postType: String) {
    var postData: PostData by remember { mutableStateOf(PostData("", false, emptyList(), "", "")) }
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            postData = async { API.getServerPostData(postType, postID) }.await()
            Log.d("POST_DATA", postData.toString())
        }
    }
    PostViewCompose(postData)
}

@Composable
fun PostViewCompose(postData: PostData){
    Column {
        Text("PostView - Title : ${postData.postTitle}")
    }
}