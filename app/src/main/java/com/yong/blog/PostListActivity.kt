package com.yong.blog

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import com.yong.blog.API.API
import com.yong.blog.API.PostList
import com.yong.blog.ui.theme.Blog_LR_AndroidTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PostListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val postType = intent.getStringExtra("postType").toString()

        setContent {
            Blog_LR_AndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    PostListUI(postType)
                }
            }
        }
    }
}

@Composable
fun PostListUI(postType: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("1인개발자 LR의 IT블로그") }
            )
        }
    ) {
        Column {
            PostListContainer(postType)
        }
    }
}

@Composable
fun PostListContainer(postType: String) {
    var postList: PostList by remember { mutableStateOf(PostList(0, emptyList())) }
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            postList = async { API.getServerPostList(postType) }.await()
        }
    }
    PostItemContainer(postList)
}

@Composable
fun PostItemContainer(postList: PostList) {
    LazyColumn {
        itemsIndexed(postList.postList) {
            index, item -> /* if(!item.postIsPinned) */ PostItem(index, item.postTitle, item.postID)
        }
    }
}

@Composable
fun PostItem(idx: Int, postTitle: String, postID: String) {
    Text("${postID} - ${postTitle}")
}