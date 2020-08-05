package ru.skillbranch.devintensive.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.core.graphics.drawable.toDrawable
import ru.skillbranch.devintensive.R

fun Context.convertDpToPx(dp: Float): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

fun Context.convertSpToPx(sp: Float): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)

fun Context.getColorAccent(): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
    return typedValue.data
}

fun Context.resolveColor(colorInt: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(colorInt, typedValue, true)
    return typedValue.data
}

fun Context.getTextAvatar(text:String): Drawable {
    val size = resources.getDimensionPixelSize(R.dimen.avatar_round_size)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

    val c = Canvas()
    c.setBitmap(bitmap)

    val halfSize = (size / 2).toFloat()

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.style = Paint.Style.FILL
    paint.color = getColorAccent()

    c.drawPaint(paint)

    val bounds = Rect()

    paint.textSize = convertSpToPx(52f)
    paint.color = resources.getColor(android.R.color.white, theme)
    paint.getTextBounds(text, 0, text.length, bounds)

    c.drawText(text, halfSize - paint.measureText(text) / 2, halfSize + bounds.height() / 2, paint)

    return bitmap.toDrawable(resources)
}


