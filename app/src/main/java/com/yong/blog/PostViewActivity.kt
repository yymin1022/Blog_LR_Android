package com.yong.blog

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.util.Base64
import android.util.Log
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
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.html.*
import io.noties.markwon.syntax.Prism4jThemeDarkula
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j
import io.noties.prism4j.annotations.PrismBundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@PrismBundle(
    include = ["brainfuck", "c", "clike", "clojure", "cpp", "csharp", "css", "dart", "git", "go",
        "groovy", "java", "javascript", "json", "kotlin", "latex", "makefile", "markdown",
        "markup", "python", "scala", "sql", "swift", "yaml"],
    grammarLocatorClassName = ".TestGrammarLocator"
)

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
    PostViewCompose(postData, postType)
}

@Composable
fun PostViewCompose(postData: PostData, postType: String) {
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
    val scope = rememberCoroutineScope()

    val htmlPlugin = HtmlPlugin.create { plugin: HtmlPlugin ->
        plugin.addHandler(TagHandlerNoOp.create("img"))
        plugin.addHandler(htmlTagHandler(ctx, scope, postType, postURL))
    }

    val syntaxHighlight = SyntaxHighlightPlugin.create(Prism4j(TestGrammarLocator()), Prism4jThemeDarkula.create())
    val markwon = Markwon.builder(ctx)
        .usePlugin(htmlPlugin)
        .usePlugin(syntaxHighlight)
        .build()

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
            it.text = markwon.toMarkdown(postContent.replace("```c++", "```cpp"))
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

class htmlTagHandler constructor(
        private val ctx: Context,
        private val scope: CoroutineScope,
        private val postType: String,
        private val postURL: String
    ): TagHandler() {
    override fun handle(visitor: MarkwonVisitor, renderer: MarkwonHtmlRenderer, tag: HtmlTag) {
        val srcName = tag.attributes()["src"].toString()

        scope.launch(Dispatchers.IO) {
            val imgData = API.getServerPostImage(postType, postURL, srcName)
            val imgByte = Base64.decode(imgData, Base64.DEFAULT)
            val bitmapData = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.size)
            val drawable = BitmapDrawable(ctx.resources, bitmapData)
            visitor.builder().setSpan(ImageSpan(drawable), tag.start())
            Log.d("IMAGE", drawable.toString())
        }
    }

    override fun supportedTags() = listOf("img")
}