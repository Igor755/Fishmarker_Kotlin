package com.company.imetlin.fishmarker.extension

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.*
import android.graphics.Matrix.ScaleToFit
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.annotation.Nullable
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.forEach
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.io.*
import java.net.URL
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sqrt


fun addKeyboardListener(activity: Activity, editText: EditText?) {
    KeyboardVisibilityEvent.setEventListener(
        activity,
        object : KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {
                if (editText != null) {
                    editText.isCursorVisible = isOpen
                }
            }
        })
}

fun ViewGroup.forEachChild(doOnItem: (View, Int) -> Unit) {
    getChildren().forEachIndexed { index, view ->
        doOnItem(view, index)
    }
}

fun isViewOverlapping(firstView: View, secondView: View): Boolean {
    val firstPosition = IntArray(2)
    val secondPosition = IntArray(2)
    firstView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    firstView.getLocationOnScreen(firstPosition)
    secondView.getLocationOnScreen(secondPosition)
    val r = firstView.measuredWidth + firstPosition[0]
    val l = secondPosition[0]
    return r >= l && r != 0 && l != 0
}

fun View.setAllClickable(clickable: Boolean) {
    isClickable = clickable
    if (this is ViewGroup) children.forEach { child -> child.setAllClickable(clickable) }
}

private fun ViewGroup.getChildren(): List<View> {
    val children = mutableListOf<View>()
    forEach {
        if (it is ViewGroup) {
            children.addAll(it.getChildren())
        } else {
            children.add(it)
        }
    }
    return children
}

fun Float.dpToPx(context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics
    )
}

fun Context.getColorCompat(@ColorRes id: Int, @Nullable theme: Resources.Theme? = this.theme): Int {
    return ResourcesCompat.getColor(resources, id, theme)
}

fun scaleBitmap(input: Bitmap?, maxBytes: Long): Bitmap? {
    val currentWidth = input?.width ?: 0
    val currentHeight = input?.height ?: 0
    val currentPixels = currentWidth * currentHeight
    // Get the amount of max pixels:
    // 1 pixel = 4 bytes (R, G, B, A)
    val maxPixels = maxBytes / 4 // Floored
    if (currentPixels <= maxPixels) {
        // Already correct size:
        return input
    }
    // Scaling factor when maintaining aspect ratio is the square root since x and y have a relation:
    val scaleFactor = sqrt(maxPixels / currentPixels.toDouble())
    val newWidthPx = floor(currentWidth * scaleFactor).toInt()
    val newHeightPx = floor(currentHeight * scaleFactor).toInt()
    return input?.let { Bitmap.createScaledBitmap(it, newWidthPx, newHeightPx, true) }
}

fun setTint(context: Context?, view: View, color: Int) {
    context?.let {
        view.background.setTint(
            ResourcesCompat.getColor(
                it.resources,
                color,
                it.theme
            )
        )
    }
}

fun decodeBitmapFromFile(
    imagePath: String?,
    reqWidth: Int,
    reqHeight: Int
): Bitmap? {

    // First decode with inJustDecodeBounds=true to check dimensions
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(imagePath, options)

    // Calculate inSampleSize
    options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight)

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false
    return BitmapFactory.decodeFile(imagePath, options)
}


fun calculateSampleSize(
    options: BitmapFactory.Options,
    reqHeight: Int,
    reqWidth: Int
): Int {

    // Raw height and width of image
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2

        // Calculate the largest inSampleSize value that is a power of 2 and
        // keeps both
        // height and width larger than the requested height and width.
        while (halfHeight / inSampleSize > reqHeight
            && halfWidth / inSampleSize > reqWidth
        ) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}

fun addWhiteBorder(
    srcBitmap: Bitmap,
    borderWidth: Int,
    borderColor: Int,
    bottomBorderWidth: Int? = null
): Bitmap {
    val dstBitmap = if (bottomBorderWidth == null) {
        Bitmap.createBitmap(
            srcBitmap.width + (borderWidth * 2),
            srcBitmap.height + (borderWidth * 2),
            Bitmap.Config.ARGB_8888
        )
    } else {
        Bitmap.createBitmap(
            srcBitmap.width + (borderWidth * 2),
            srcBitmap.height + (borderWidth + bottomBorderWidth),
            Bitmap.Config.ARGB_8888
        )
    }

    val canvas = Canvas(dstBitmap)

    canvas.drawColor(borderColor)
    canvas.drawBitmap(srcBitmap, borderWidth.toFloat(), borderWidth.toFloat(), null)
    srcBitmap.recycle()
    return dstBitmap
}

fun Bitmap.centerCrop(maxHeight: Int, maxWidth: Int, polaroid: Boolean): Bitmap {
    var mMaxHeight = maxHeight
    var mMaxWidth = maxWidth
    val mDrawMatrix = Matrix()
    val scale: Float
    var dx = 0f
    var dy = 0f

    val polaroidBorder = (if (polaroid) maxHeight * 0.2f else 0f).roundToInt()
    val border =  (if (polaroid) maxWidth * 0.05f else 0f).roundToInt()


    val res = Bitmap.createBitmap(mMaxWidth, mMaxHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(res)

    if (width * mMaxHeight > mMaxWidth * height) {
        scale = mMaxHeight.toFloat() / height.toFloat()
        dx = (mMaxWidth - width * scale) * 0.5f
    } else {
        scale = mMaxWidth.toFloat() / width.toFloat()
        dy = (mMaxHeight - height * scale) * 0.5f
    }
    mDrawMatrix.setScale(scale, scale)
    mDrawMatrix.postTranslate(
        dx.roundToInt().toFloat(),
        dy.roundToInt().toFloat()
    )
    canvas.drawBitmap(this, mDrawMatrix, null)
    return if (border != 0)
    addWhiteBorder(res, border, Color.WHITE, polaroidBorder)
    else res
}

fun Bitmap.centerInside(maxHeight: Int, maxWidth: Int, polaroid: Boolean): Bitmap {
    var mMaxHeight = maxHeight
    var mMaxWidth = maxWidth

    // recreate the new Bitmap
    val res = Bitmap.createBitmap(mMaxWidth, mMaxHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(res)

    val mDrawMatrix = Matrix()

    // Generate the required transform.
    val mTempSrc = RectF()
    val mTempDst = RectF()

    val polaroidBorder = if (polaroid) maxHeight * 0.2f else 0f
    mTempSrc.set(0f, 0f, width.toFloat(), height.toFloat())

    mTempDst.set(0f, 0f, maxWidth.toFloat(), maxHeight.toFloat() - polaroidBorder)

    mDrawMatrix.setRectToRect(
        mTempSrc,
        mTempDst,
        ScaleToFit.CENTER
    )

    canvas.drawColor(Color.WHITE)
    canvas.drawBitmap(this, mDrawMatrix, null)
    // recycle()
    return res
}


fun getCroppedBitmap(image: Bitmap, maxHeight: Int, maxWidth: Int): Bitmap {
    var mMaxHeight = maxHeight
    var mMaxWidth = maxWidth
    var width = image.width
    var height = image.height

    // create a matrix for the manipulation

    // recreate the new Bitmap
    val res = Bitmap.createBitmap(mMaxWidth, mMaxHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(res)
    val endHorizontal = mMaxWidth > mMaxHeight
    val startHorizontal = width > height
    var w = (mMaxWidth - width) / 2
    var h = (mMaxHeight - height) / 2

    val mDrawMatrix = Matrix()
    val scale: Float
    var dx = 0f
    var dy = 0f

    if (width * maxHeight > maxWidth * height) {
        scale = maxHeight.toFloat() / height.toFloat()
        dx = (maxWidth - width * scale) * 0.5f
    } else {
        scale = maxWidth.toFloat() / width.toFloat()
        dy = (maxHeight - height * scale) * 0.5f
    }

    mDrawMatrix.setScale(scale, scale)
    mDrawMatrix.postTranslate(
        dx.roundToInt().toFloat(),
        dy.roundToInt().toFloat()
    )
    canvas.drawBitmap(image, mDrawMatrix, null)
    return res
}

fun getResizedBitmap(image: Bitmap, maxHeight: Int, maxWidth: Int): Bitmap {
    var width = image.width
    var height = image.height
    when {
        width > height -> {
            // landscape
            val ratio = width.toFloat() / maxWidth
            width = maxWidth
            height = (height / ratio).toInt()
        }
        height > width -> {
            // portrait
            val ratio = height.toFloat() / maxHeight
            height = maxHeight
            width = (width / ratio).toInt()
        }
        else -> {
            // square
            height = maxHeight
            width = maxWidth
        }
    }
    // create a matrix for the manipulation

    // recreate the new Bitmap
    return Bitmap.createScaledBitmap(
        image, width, height, true
    )
}

fun Bitmap.insertIntoFrame(aspectX: Int, aspectY: Int, polaroid: Boolean = false): Bitmap? {
    var mAspectX = aspectX
    var mAspectY = aspectY
    if (mAspectX >= mAspectY != width >= height) {
        mAspectX = aspectY
        mAspectY = aspectX
    }
    val step = width / mAspectX
    val height = step * mAspectY
    val width = step * mAspectX

    return centerCrop(
        height,
        width,
        polaroid
    )
}

private fun Bitmap.process(downscale: Boolean, limitSizeTo: Int = 1900): Bitmap {
    var bitmap = this
    if (downscale && this != null && arrayOf(width, height).max() ?: 0 > 1000) {
        val tempBitmap = bitmap
        bitmap = Bitmap.createScaledBitmap(this, width / 4, height / 4, false)
        tempBitmap.recycle()
    } else if (limitSizeTo != 0 && bitmap != null && arrayOf(
            bitmap.width,
            bitmap.height
        ).min() ?: 0 > limitSizeTo
    ) {
        val tempBitmap = bitmap
        bitmap = getResizedBitmap(bitmap, 1900, 1900)
        tempBitmap.recycle()
    }
    return bitmap
}

@Throws(FileNotFoundException::class)
fun getBitmapFromPath(
    context: Context,
    path: String?,
    downscale: Boolean = false,
    limitSizeTo: Int = 1900
): Bitmap? {
    var bitmap: Bitmap? = null
    when {
        path!!.startsWith("https://") -> {
            try {
                val url = URL(path)
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                options.inMutable = true
                bitmap =
                    BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options)
            } catch (e: IOException) {
                throw FileNotFoundException()
            }
            bitmap = bitmap?.process(downscale, limitSizeTo)
        }
        path.startsWith("content:") -> {
            bitmap = getBitmapFromUri(context, Uri.parse(path))
            bitmap = bitmap?.process(downscale, limitSizeTo)
        }
        else -> {
            try {
                val f = File(path)
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                options.inMutable = true
                bitmap = BitmapFactory.decodeStream(FileInputStream(f), null, options)
                bitmap = bitmap?.process(downscale, limitSizeTo)
                //file path means that it is original image. we can rotate it and it will  affect on all image copies
                bitmap = bitmap?.let {
                    rotateImageIfRequired(
                        it,
                        path
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    return bitmap
}

/**
 * @param selectedImage source path
 * @param orientation ignore exif orientation and use provided one
 */
@Throws(IOException::class)
private fun rotateImageIfRequired(
    img: Bitmap,
    selectedImage: String,
    orientation: Int? = null
): Bitmap? {

    val mOrientation: Int =
        if (orientation != null) {
            orientation
        } else {
            val ei = ExifInterface(selectedImage)
            ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        }
    return when (mOrientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
        else -> img
    }
}

@Throws(FileNotFoundException::class)
fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(
                context.contentResolver,
                uri
            )
        ) { decoder, info, source ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            decoder.isMutableRequired = true
        }
    } else {
        context.contentResolver.openInputStream(uri)
            .use { inputStream ->
                BitmapFactory.decodeStream(inputStream).copy(Bitmap.Config.ARGB_8888, true)
            }
    }
}


//fun getImageUriFromBitmap(context: Context, bitmap: Bitmap?): Uri {
//    val bytes = ByteArrayOutputStream()
//    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//    val path = MediaStore.Images.Media.insertImage(
//        context.contentResolver,
//        bitmap,
//        "IMG_" + System.currentTimeMillis(),
//        null
//    )
//    return Uri.parse(path)
//}

//fun getImageUriFromBitmap(context: Context, bitmap: Bitmap?): Uri {
//    val bytes = ByteArrayOutputStream()
//    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//
//    val cw = ContextWrapper(context)
//    // path to /data/data/yourapp/app_data/imageDir
//    val directory = cw.getDir("PicsetImages", Context.MODE_PRIVATE)
//    // Create imageDir
//    val mypath = File(directory, "IMG_${System.currentTimeMillis()}.jpg")
//    val url = if (mypath.length() > 0) {
//        mypath.absolutePath
//    } else null
//    return Uri.parse(url)
//}

fun getTempImageUriFromBitmap(
    mContext: Context,
    bitmap: Bitmap?
): Uri? {
    val wrapper = ContextWrapper(mContext)
    var file = wrapper.getDir("PicsetImages", Context.MODE_PRIVATE)

    val outputDir: File = mContext.cacheDir // context being the Activity pointer
    file = File.createTempFile("IMG_${System.currentTimeMillis()}", ".jpg", outputDir)

    try {
        var stream: OutputStream? = null
        stream = FileOutputStream(file)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return Uri.parse(file.absolutePath)
}

fun getImageUriFromBitmap(
    projectId: Long,
    id: Long,
    mContext: Context,
    bitmap: Bitmap?,
    origin: Boolean = false
): Uri? {
    val wrapper = ContextWrapper(mContext)
    var file = wrapper.getDir("PicsetImages", Context.MODE_PRIVATE)
    file = File(file, "IMG_${if (origin) "o" else ""}${projectId}_$id.jpg")

    try {
        var stream: OutputStream? = null
        stream = FileOutputStream(file)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return Uri.parse(file.absolutePath)
}

fun getBitmapFromView(view: View): Bitmap? {
    //Define a bitmap with the same size as the view
    val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    //Bind a canvas to it
    val canvas = Canvas(returnedBitmap)
    //Get the view's background
    val bgDrawable = view.background
    if (bgDrawable != null) {
        //has background drawable, then draw it on the canvas
        bgDrawable.draw(canvas)
    } else {
        //does not have background drawable, then draw white background on the canvas
        canvas.drawColor(Color.WHITE)
    }
    // draw the view on the canvas
    view.draw(canvas)
    //return the bitmap
    return returnedBitmap
}

fun Context?.isNetworkConnected(): Boolean {
    val cm: ConnectivityManager? =
        this?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    return cm?.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
}