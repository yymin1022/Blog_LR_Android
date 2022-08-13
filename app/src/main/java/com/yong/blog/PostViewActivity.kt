package com.yong.blog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yong.blog.api.API
import com.yong.blog.api.PostData
import com.yong.blog.ui.theme.Blog_LR_AndroidTheme
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
fun PostViewUI(postID: String, postType: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("1인개발자 LR의 IT블로그") }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            PostViewContainer(postID, postType)
        }
    }
}

@Composable
fun PostViewContainer(postID: String, postType: String) {
    val (postData, setPostData) = remember { mutableStateOf(PostData("", "", false, emptyList(), "", "")) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            setPostData(API.getServerPostData(postType, postID))
        }
    }
    PostViewCompose(postData)
}

@Composable
fun PostViewCompose(postData: PostData) {
    Column {
        PostViewTitle(postData.postTitle)
        PostViewDate(postData.postDate)
        PostViewTag(postData.postTag)
        PostViewContent(postData.postContent)
    }
}

@Composable
fun PostViewContent(postContent: String) {
    MarkdownText(markdown = postContent)
}

@Composable
fun PostViewDate(postDate: String) {
    Text("Date : $postDate")
}

@Composable
fun PostViewTag(postTag: List<String>) {
    Text("Tag : $postTag")
}

@Composable
fun PostViewTitle(postTitle: String) {
    Text("Title : $postTitle")
}