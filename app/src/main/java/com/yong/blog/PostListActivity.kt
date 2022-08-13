package com.yong.blog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yong.blog.api.API
import com.yong.blog.api.PostList
import com.yong.blog.api.PostListItem
import com.yong.blog.ui.theme.BlogBlue500
import com.yong.blog.ui.theme.Blog_LR_AndroidTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            PostListContainer(postType)
        }
    }
}

@Composable
fun PostListContainer(postType: String) {
    val (postList, setPostList) = remember { mutableStateOf(PostList(0, emptyList())) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            setPostList(API.getServerPostList(postType))
        }
    }
    PostItemContainer(postType, postList)
}

@Composable
fun PostItemContainer(postType: String, postList: PostList) {
    LazyColumn {
        itemsIndexed(postList.postList) {
            index, item -> if(item.postIsPinned) PostItem(index, item, postType)
        }
        itemsIndexed(postList.postList) {
            index, item -> if(!item.postIsPinned) PostItem(index, item, postType)
        }
    }
}

@Composable
fun PostItem(idx: Int, postItem: PostListItem, postType: String) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .border(width = 2.dp, color = Color.LightGray)
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onPostItemClicked(postItem.postID, postType, context) },
    ) {
        Row {
            PostItemImage(postItem.postID, postType)
            PostItemData(postItem)
        }
    }
}

@Composable
fun PostItemImage(postID: String, postType: String) {
    Box(
        modifier = Modifier
            .height(100.dp)
            .width(100.dp)
    ) {
        Text("IMAGE")
    }
}

@Composable
fun PostItemData(postItem: PostListItem) {
    Column {
        PostItemDataTitle(postItem.postTitle)
        PostItemDataDate(postItem.postDate)
        PostItemDataTag(postItem.postTag)
    }
}

@Composable
fun PostItemDataDate(postDate: String) {
    Text(
        text = postDate,
        style = TextStyle(

        )
    )
}

@Composable
fun PostItemDataTag(postTag: List<String>) {
    Text(
        text = postTag.toString(),
        style = TextStyle(

        )
    )
}

@Composable
fun PostItemDataTitle(postTitle: String) {
    Text(
        text = postTitle,
        style = TextStyle(
            color = BlogBlue500,
            fontSize = 17.sp
        )
    )
}

fun onPostItemClicked(postID: String, postType: String, context: Context){
    val intent = Intent(context, PostViewActivity::class.java)
    intent.putExtra("postID", postID)
    intent.putExtra("postType", postType)
    context.startActivity(intent)
}