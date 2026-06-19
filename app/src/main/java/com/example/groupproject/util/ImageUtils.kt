package com.example.groupproject.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageUtils {

    fun saveImageToInternalStorage(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Cannot open image stream")

        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        // Scale down large images to save space
        val scaledBitmap = scaleDown(bitmap, 512)
        if (scaledBitmap !== bitmap) {
            bitmap.recycle()
        }

        val fileName = "friend_${UUID.randomUUID()}.jpg"
        val dir = File(context.filesDir, "images")
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val file = File(dir, fileName)
        FileOutputStream(file).use { out ->
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }
        scaledBitmap.recycle()

        return file.absolutePath
    }

    fun deleteImage(path: String) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun scaleDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) {
            return bitmap
        }
        val ratio = minOf(maxDimension.toFloat() / width, maxDimension.toFloat() / height)
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
