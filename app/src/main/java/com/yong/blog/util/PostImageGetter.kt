package com.yong.blog.util

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.util.Base64
import androidx.compose.runtime.mutableStateOf
import com.yong.blog.api.API
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostImageGetter(
        private val ctx: Context,
        private val scope: CoroutineScope,
        private val postType: String,
        private val postURL: String
    ): Html.ImageGetter {
    override fun getDrawable(source: String): Drawable {
        val (bitmap, setBitmap) = mutableStateOf(BitmapFactory.decodeByteArray(ByteArray(0), 0, 0))
        val (drawable, setDrawable) = mutableStateOf(BitmapDrawable(ctx.resources, bitmap))
        val (imgByte, setImgByte) = mutableStateOf(ByteArray(0))
        val (imgData, setImgData) = mutableStateOf("")

        scope.launch(Dispatchers.IO) {
            setImgData(API.getServerPostImage(postType, postURL, source))
            setImgByte(Base64.decode(imgData, Base64.DEFAULT))
            setBitmap(BitmapFactory.decodeByteArray(imgByte, 0, imgByte.size))
            setDrawable(BitmapDrawable(ctx.resources, bitmap))
        }

        return drawable
    }
}