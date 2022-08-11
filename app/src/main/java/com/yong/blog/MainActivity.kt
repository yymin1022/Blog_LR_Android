package com.yong.blog

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.yong.blog.ui.theme.*

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Blog_LR_AndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainUI()
                }
            }
        }
    }
}

@Composable
fun MainUI() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("1인개발자 LR의 IT블로그") }
            )
        }
    ) {
        Column {
            MainTitleContainer()
            MainButtonContainer()
        }

    }
}

@Composable
fun MainButtonContainer() {
    val context = LocalContext.current
    Column {
        MainButtonItem("Blog") {
            val intent = Intent(context, PostListActivity::class.java)
            intent.putExtra("postType", "blog")
            context.startActivity(intent)
        }
        MainButtonItem("Project") {
            val intent = Intent(context, PostListActivity::class.java)
            intent.putExtra("postType", "project")
            context.startActivity(intent)
        }
        MainButtonItem("Problem Solving") {
            val intent = Intent(context, PostListActivity::class.java)
            intent.putExtra("postType", "solving")
            context.startActivity(intent)
        }
        MainButtonItem("About") {
            val intent = Intent(context, PostViewActivity::class.java)
            intent.putExtra("postType", "about")
            intent.putExtra("postID", "LR")
            context.startActivity(intent)
        }
    }
}

@Composable
fun MainButtonItem(text: String, action: () -> Unit) {
    OutlinedButton(
        onClick = { action() }
    ) {
        Text(
            text = text,
            color = Color.Black
        )
    }
}

@Composable
fun MainTitleContainer() {
    Column {
        MainTitleText("안녕하세요", false)
        MainTitleText("대학생 1인개발자", true)
        MainTitleText("LR입니다", false)
    }
}

@Composable
fun MainTitleText(text: String, isBlue: Boolean) {
    if(isBlue){
        Text(
            text = text,
            color = BlogBlue500
        )
    }else{
        Text(
            text = text
        )
    }
}