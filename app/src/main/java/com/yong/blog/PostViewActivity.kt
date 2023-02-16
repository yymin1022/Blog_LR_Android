package com.yong.blog

import android.os.Bundle
import android.text.method.LinkMovementMethod
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
import coil.ImageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.yong.blog.api.API
import com.yong.blog.api.PostData
import com.yong.blog.ui.theme.Blog_LR_AndroidTheme
import com.yong.blog.util.PostImageGetter
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.RenderProps
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.TagHandlerNoOp
import io.noties.markwon.html.tag.SimpleTagHandler
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.syntax.Prism4jThemeDarkula
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j
import io.noties.prism4j.annotations.PrismBundle
import kotlinx.coroutines.Dispatchers
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

    val imageLoader = ImageLoader.Builder(ctx)
        .apply {
            availableMemoryPercentage(0.5)
            bitmapPoolPercentage(0.5)
            crossfade(true)
        }
        .build()
    val coilPlugin = CoilImagesPlugin.create(
        object : CoilImagesPlugin.CoilStore {
            override fun load(drawable: AsyncDrawable): ImageRequest {
                return ImageRequest.Builder(ctx)
                    .defaults(imageLoader.defaults)
                    .data(drawable.destination)
                    .crossfade(true)
                    .transformations(CircleCropTransformation())
                    .build()
            }

            override fun cancel(disposable: Disposable) {
                disposable.dispose()
            }
        },
        imageLoader)

    val htmlPlugin = HtmlPlugin.create { plugin: HtmlPlugin ->
        plugin.addHandler(TagHandlerNoOp.create("img"))
        plugin.addHandler(htmlTagHandler())
    }

    val syntaxHighlight = SyntaxHighlightPlugin.create(Prism4j(TestGrammarLocator()), Prism4jThemeDarkula.create())
    val markwon = Markwon.builder(ctx)
        .usePlugin(coilPlugin)
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

class htmlTagHandler: SimpleTagHandler() {
    override fun supportedTags() = listOf("img")
    override fun getSpans(
        configuration: MarkwonConfiguration,
        renderProps: RenderProps,
        tag: HtmlTag
    ): Any? {
        val srcName = tag.attributes()["src"].toString()
        Log.d("IMAGE_RENDER", srcName)
        return null
    }
}