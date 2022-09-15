package com.github.sikv.photos.common.ui

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.text.TextPaint
import androidx.annotation.ColorInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

object TextPlaceholder {

    enum class Shape {
        NONE,
        CIRCLE
    }

    fun with(context: Context) = PlaceholderGeneratorBuilder(context)
}

class PlaceholderGeneratorBuilder(private val context: Context) {

    private var text: String? = null
    private var textColor = Color.WHITE

    private var shape = TextPlaceholder.Shape.NONE
    private var backgroundColor = Color.BLACK

    fun text(text: String) = apply {
        this.text = text
    }

    fun textInitials(text: String) = apply {
        this.text = text.getInitials()
    }

    fun textFirstChar(text: String) = apply {
        this.text = text.first().uppercaseChar().toString()
    }

    fun textColor(@ColorInt color: Int) = apply {
        this.textColor = color
    }

    fun background(shape: TextPlaceholder.Shape, @ColorInt backgroundColor: Int) = apply {
        this.shape = shape
        this.backgroundColor = backgroundColor
    }

    suspend fun build(): BitmapDrawable? = withContext(Dispatchers.IO) {
        text?.let { t ->
            val generator = PlaceholderGenerator(t, textColor, shape, backgroundColor)
            val bitmap = generator.generate()

            BitmapDrawable(context.resources, bitmap)
        } ?: run {
            null
        }
    }
}

private class PlaceholderGenerator(
    val text: String,
    val textColor: Int,
    val shape: TextPlaceholder.Shape,
    val backgroundColor: Int
) {

    companion object {
        private const val W = 100
        private const val H = 100
    }

    fun generate(): Bitmap {
        val bitmap = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        if (shape != TextPlaceholder.Shape.NONE) {
            val bgPaint = Paint()

            bgPaint.isAntiAlias = true
            bgPaint.style = Paint.Style.FILL
            bgPaint.color = backgroundColor

            canvas.drawCircle(
                (W / 2).toFloat(),
                (H / 2).toFloat(),
                (W / 2).toFloat(),
                bgPaint
            )
        }

        val textPaint = TextPaint()

        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL
        textPaint.color = textColor
        textPaint.typeface = Typeface.DEFAULT_BOLD
        textPaint.textSize = 50F

        drawTextCenter(canvas, textPaint, text)

        return bitmap
    }

    private fun drawTextCenter(canvas: Canvas, paint: Paint, text: String) {
        val rect = Rect()

        canvas.getClipBounds(rect)

        val cHeight: Int = rect.height()
        val cWidth: Int = rect.width()

        paint.textAlign = Paint.Align.LEFT
        paint.getTextBounds(text, 0, text.length, rect)

        val x: Float = cWidth / 2F - rect.width() / 2F - rect.left
        val y: Float = cHeight / 2F + rect.height() / 2F - rect.bottom

        canvas.drawText(text, x, y, paint)
    }
}

private fun String.getInitials(): String {
    val strings = split(" ", limit = 2)

    val initials = if (strings.size >= 2) {
        "${strings[0].trim().first()}${strings[1].trim().first()}"
    } else {
        val string0 = strings[0].trim()

        if (string0.length >= 2) {
            "${string0[0]}${string0[1]}"
        } else {
            string0[0].toString()
        }
    }

    return initials.uppercase(Locale.ROOT)
}
