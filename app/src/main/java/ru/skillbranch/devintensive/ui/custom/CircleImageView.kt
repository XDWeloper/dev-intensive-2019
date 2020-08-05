package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import ru.skillbranch.devintensive.R
import kotlin.math.min
import kotlin.math.roundToInt

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_BORDER_WIDTH = 2f
        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
        private val SCALE_TYPE = ScaleType.CENTER_CROP
    }

    private var mBitmapShader: BitmapShader? = null
    private var mBitmap: Bitmap? = null
    private var mBorderBounds: RectF
    private var mBitmapDrawBounds: RectF
    private var mBorderPaint: Paint
    private var mBitmapPaint: Paint
    private var mShaderMatrix: Matrix
    private var mBorderColor = DEFAULT_BORDER_COLOR
    private var mBorderWidth = DEFAULT_BORDER_WIDTH

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, 0, 0)

            mBorderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            mBorderWidth = a.getDimension(R.styleable.CircleImageView_cv_borderWidth, DEFAULT_BORDER_WIDTH)

            a.recycle()
        }

        mShaderMatrix = Matrix()
        mBorderBounds = RectF()
        mBitmapDrawBounds = RectF()
        mBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        setupBitmap()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        setupBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        setupBitmap()
    }

    override fun setImageBitmap(bitmap: Bitmap) {
        super.setImageBitmap(bitmap)
        setupBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        setupBitmap()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        val halfBorderWidth = mBorderPaint.strokeWidth / 2f
        updateCircleDrawBounds(mBitmapDrawBounds)
        mBorderBounds.set(mBitmapDrawBounds)
        mBorderBounds.inset(halfBorderWidth, halfBorderWidth)

        updateBitmap()
    }

    override fun onDraw(canvas: Canvas) {
        drawBitmap(canvas)
        drawBorder(canvas)
    }

    @Dimension
    fun getBorderWidth(): Int = mBorderWidth.roundToInt()

    fun setBorderWidth(@Dimension dp: Int) {
        mBorderWidth = dp.toFloat()

        updateBitmap()
    }

    fun getBorderColor(): Int = mBorderColor

    fun setBorderColor(hex: String) {
        mBorderColor = Color.parseColor(hex)

        updateBitmap()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        mBorderColor = resources.getColor(colorId, context.theme)
    }

    private fun drawBorder(canvas: Canvas) {
        if (mBorderPaint.strokeWidth > 0f) {
            canvas.drawOval(mBorderBounds, mBorderPaint)
        }
    }

    private fun drawBitmap(canvas: Canvas) {
        canvas.drawOval(mBitmapDrawBounds, mBitmapPaint)
    }

    private fun updateCircleDrawBounds(bounds: RectF) {
        val contentWidth = (width - paddingLeft - paddingRight).toFloat()
        val contentHeight = (height - paddingTop - paddingBottom).toFloat()

        var left = paddingLeft.toFloat()
        var top = paddingTop.toFloat()
        if (contentWidth > contentHeight) {
            left += (contentWidth - contentHeight) / 2f
        } else {
            top += (contentHeight - contentWidth) / 2f
        }

        val diameter = min(contentWidth, contentHeight)
        bounds.set(left, top, left + diameter, top + diameter)
    }

    private fun setupBitmap() {
        super.setScaleType(SCALE_TYPE)

        mBitmap = getBitmapFromDrawable(drawable)
        if (mBitmap == null) {
            return
        }

        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mBitmapPaint.shader = mBitmapShader

        updateBitmap()
    }

    private fun updateBitmap() {
        if (mBitmap == null) return

        val dx: Float
        val dy: Float
        val scale: Float

        mBorderPaint.color = mBorderColor
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.strokeWidth = mBorderWidth

        // scale up/down with respect to this view size and maintain aspect ratio
        // translate bitmap position with dx/dy to the center of the image
        if (mBitmap!!.width < mBitmap!!.height) {
            scale = mBitmapDrawBounds.width() / mBitmap!!.width
            dx = mBitmapDrawBounds.left
            dy = mBitmapDrawBounds.top - mBitmap!!.height * scale / 2f + mBitmapDrawBounds.width() / 2f
        } else {
            scale = mBitmapDrawBounds.height() / mBitmap!!.height
            dx = mBitmapDrawBounds.left - mBitmap!!.width * scale / 2f + mBitmapDrawBounds.width() / 2f
            dy = mBitmapDrawBounds.top
        }
        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate(dx, dy)
        mBitmapShader?.setLocalMatrix(mShaderMatrix)
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            BITMAP_CONFIG
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}