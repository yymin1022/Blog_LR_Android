package com.yong.blog

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import com.yong.blog.API.API
import com.yong.blog.API.PostList
import com.yong.blog.ui.theme.Blog_LR_AndroidTheme

class PostListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val postType = intent.getStringExtra("postType").toString()

        val postList = API.getServerPostList(postType)
        Log.d("POST_LIST", "Count : ${postList.postCount} List : ${postList.postList}")

        setContent {
            Blog_LR_AndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    PostListUI(postList)
                }
            }
        }
    }
}

@Composable
fun PostListUI(postList: PostList) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("1인개발자 LR의 IT블로그") }
            )
        }
    ) {
        Column {
            PostItemContainer(postList)
        }
    }
}

@Composable
fun PostItemContainer(postList: PostList) {
    LazyColumn {
        item {
            PostItem("TEST 1", "test1")
            PostItem("TEST 2", "test2")
        }
    }
}

@Composable
fun PostItem(postTitle: String, postID: String) {
    Text("Post Item ${postID}")
}