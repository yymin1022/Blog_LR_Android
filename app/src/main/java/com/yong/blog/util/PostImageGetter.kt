package com.yong.blog.util

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.util.Base64
import androidx.compose.runtime.mutableStateOf
import com.yong.blog.api.API
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PostImageGetter(
        private val ctx: Context,
        private val postType: String,
        private val postURL: String
    ): Html.ImageGetter {
    override fun getDrawable(source: String): Drawable {
        val (imageData, setImageData) = mutableStateOf("")

        GlobalScope.launch(Dispatchers.IO) {
            runCatching {
                setImageData(API.getServerPostImage(postType, postURL, source))
            }
        }

        val imageBytes = Base64.decode(imageData, Base64.DEFAULT)
        val bitmapData = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        return BitmapDrawable(ctx.resources, bitmapData)
    }
}