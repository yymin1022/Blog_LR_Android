package com.yong.blog.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.util.Base64
import android.util.Log
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
        lateinit var bitmapData: Bitmap
        lateinit var drawable: Drawable

        scope.launch(Dispatchers.IO) {
            val imgData = API.getServerPostImage(postType, postURL, source)
            val imgByte = Base64.decode(imgData, Base64.DEFAULT)
            bitmapData = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.size)
            drawable = BitmapDrawable(ctx.resources, bitmapData)
            Log.d("IMAGE", drawable.toString())
        }

        return drawable
    }
}