package com.yong.blog

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
                    modifier = Modifier
                        .fillMaxSize(),
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
                title = {
                    Text("1인개발자 LR의 IT블로그")
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 15.dp)
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
            _, item -> if(item.postIsPinned) PostItem(item, postType)
        }
        itemsIndexed(postList.postList) {
            _, item -> if(!item.postIsPinned) PostItem(item, postType)
        }
    }
}

@Composable
fun PostItem(postItem: PostListItem, postType: String) {
    val context = LocalContext.current
    Column {
        Spacer(Modifier
            .height(5.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .shadow(3.dp, RectangleShape)
                .clickable { onPostItemClicked(postItem.postID, postType, context) },
        ) {
            Row {
                PostItemImage(postItem.postURL, postType, postItem.postIsPinned)
                PostItemData(postItem)
            }
        }
        Spacer(Modifier.height(5.dp))
    }
}

@Composable
fun PostItemImage(postURL: String, postType: String, postIsPinned: Boolean) {
    val (imageData, setImageData) = remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val thumbFile = when(postType) {
                "solving" -> when {
                    postIsPinned -> "thumb_boj.png"
                    else -> "thumb_programmers.png"
                }
                else -> "thumb.png"
            }
            setImageData(API.getServerPostImage(postType, postURL, thumbFile))
        }
    }
    val imageBytes = Base64.decode(imageData, Base64.DEFAULT)
    val bitmapData = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    Box(
        modifier = Modifier
            .height(100.dp)
            .width(100.dp)
    ) {
        if(bitmapData != null) {
            Image(
                bitmap = bitmapData.asImageBitmap(),
                contentDescription = "Thumbnail"
            )
        }
    }
}

@Composable
fun PostItemData(postItem: PostListItem) {
    Column(
        modifier = Modifier
            .padding(7.dp)
    ) {
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
            color = Color.Gray,
            fontSize = 17.sp
        )
    )
}

@Composable
fun PostItemDataTag(postTag: List<String>) {
    Row {
        for(tagItem in postTag) {
            Text(
                text = "#$tagItem ",
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 17.sp
                )
            )
        }
    }
}

@Composable
fun PostItemDataTitle(postTitle: String) {
    Text(
        text = postTitle,
        modifier = Modifier
            .height(45.dp),
        style = TextStyle(
            color = BlogBlue500,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    )
}

fun onPostItemClicked(postID: String, postType: String, context: Context){
    val intent = Intent(context, PostViewActivity::class.java)
    intent.putExtra("postID", postID)
    intent.putExtra("postType", postType)
    context.startActivity(intent)
}