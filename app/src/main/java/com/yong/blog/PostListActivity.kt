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
import androidx.compose.runtime.Composable
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

        CoroutineScope(Dispatchers.IO).launch{
            val postList = async { API.getServerPostList(postType) }
            Log.d("POST_LIST", "Count : ${postList.await().postCount} List : ${postList.await().postList}")
        }

        setContent {
            Blog_LR_AndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
//                    PostListUI(postList)
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
        itemsIndexed(postList.postList) {
            index, item -> PostItem(index, item.postTitle, item.postID)
        }
    }
}

@Composable
fun PostItem(idx: Int, postTitle: String, postID: String) {
    Text("Post Item ${postID}")
}