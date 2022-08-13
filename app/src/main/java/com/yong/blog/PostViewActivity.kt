package com.yong.blog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yong.blog.api.API
import com.yong.blog.api.PostData
import com.yong.blog.ui.theme.BlogBlue500
import com.yong.blog.ui.theme.Blog_LR_AndroidTheme
import io.noties.markwon.Markwon
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        PostViewTitle(postData.postTitle)

        Row {
            Text("written by LR")
            Text(" | ")
            PostViewDate(postData.postDate)
        }

        PostViewContent(postData.postContent)
        PostViewTag(postData.postTag)
    }
}

@Composable
fun PostViewContent(postContent: String) {
    val ctx = LocalContext.current
    val markwon: Markwon = Markwon.create(ctx)
    val txtMD = markwon.toMarkdown(postContent)

    Text(txtMD.toString())
}

@Composable
fun PostViewDate(postDate: String) {
    Text(
        modifier = Modifier,
        text = postDate
    )
}

@Composable
fun PostViewTag(postTag: List<String>) {
    Text(postTag.toString())
}

@Composable
fun PostViewTitle(postTitle: String) {
    Text(
        text = postTitle,
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    )
}