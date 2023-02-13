package com.yong.blog

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.yong.blog.api.API
import com.yong.blog.api.PostData
import com.yong.blog.ui.theme.Blog_LR_AndroidTheme
import com.yong.blog.util.PostImageGetter
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
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
                    modifier = Modifier
                        .fillMaxSize(),
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
                title = {
                    Text("1인개발자 LR의 IT블로그")
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
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
    PostViewCompose(postData, postID, postType)
}

@Composable
fun PostViewCompose(postData: PostData, postID: String, postType: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 30.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(25.dp)
    ) {
        PostViewTitle(postData.postTitle)

        Row {
            PostViewDate(postData.postDate)
        }

        Divider(
            color = Color.LightGray
        )

        PostViewContent(postData.postContent, postData.postURL, postType)
        PostViewTag(postData.postTag)
    }
}

@Composable
fun PostViewContent(postContent: String, postURL: String, postType: String) {
    val ctx = LocalContext.current
    val imageGetter = PostImageGetter(ctx, rememberCoroutineScope(), postType, postURL)
    val markwon = Markwon.create(ctx)

    AndroidView(
        modifier = Modifier
            .fillMaxSize(),
        factory = {
            context -> TextView(context).apply {
                setTextColor(Color.Black.hashCode())
                movementMethod = LinkMovementMethod.getInstance()
            }
        },
        update = {
//            // obtain an instance of Markwon
//            final Markwon markwon = Markwon.create(context);
//
//// parse markdown and create styled text
//            final Spanned markdown = markwon.toMarkdown("**Hello there!**");
            it.text = markwon.toMarkdown(postContent)
        }
    )
}

@Composable
fun PostViewDate(postDate: String) {
    Text(
        text = "written by LR | $postDate",
        style = TextStyle(
            fontSize = 15.sp
        )
    )
}

@Composable
fun PostViewTag(postTag: List<String>) {
    Row{
        for(tag in postTag){
            Text(
                text = "#$tag ",
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 15.sp
                )
            )
        }
    }
}

@Composable
fun PostViewTitle(postTitle: String) {
    Text(
        text = postTitle,
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    )
}