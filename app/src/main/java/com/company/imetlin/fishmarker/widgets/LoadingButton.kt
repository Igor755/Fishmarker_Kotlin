package com.company.imetlin.fishmarker.widgets

import android.animation.AnimatorInflater
import android.animation.StateListAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ViewSwitcher
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.graphics.withSave
import androidx.core.view.*
import androidx.core.widget.TextViewCompat
import com.company.imetlin.fishmarker.R
import com.company.imetlin.fishmarker.extension.dpToPx
import kotlinx.android.synthetic.main.widget_loading_button.view.*

class LoadingButton : ViewSwitcher, View.OnClickListener {

    private var strokeColor: Int = 0
    private var insetLeft: Int = 0
    private var insetTop: Int = 0
    private var insetBottom: Int = 0
    private var insetRight: Int = 0

    private val roundPaint = object : Paint() {
        init {
            isAntiAlias = true
            style = Style.STROKE
            strokeWidth = 2f.dpToPx(context)
        }
    }

    var cornerRadius: Float = 0.toFloat()
        private set(value) {
            field = value
            if (value > 0.0F) setLayerType(View.LAYER_TYPE_HARDWARE, null)
            else setLayerType(View.LAYER_TYPE_NONE, null)
        }

    var isClickableOnLoading = false
        set(value) {
            if (field == value) return
            field = value
            invalidateButtonClickable()
        }

    var isLoading: Boolean = displayedChild == LOADING_POSITION
        set(value) {
            field = value
            if (field == isLoading) return
            displayedChild = if (field) {
                removeCallbacks(stopSpinRunnable)
                pwLoadingButton.spin()
                tvLoadingButton.isVisible = false
                LOADING_POSITION
            } else {
                // stop spinning little bit later
                postDelayed(
                    stopSpinRunnable,
                    250L
                )
                TITLE_POSITION
            }
            invalidateButtonClickable()
        }
        get() = displayedChild == LOADING_POSITION

    private val stopSpinRunnable = Runnable { pwLoadingButton?.stopSpinning() }

    private var innerClickListener: OnClickListener? = null
    var onLoadingButtonClickListener: OnLoadingButtonClickListener? = null
        set(value) {
            if (field == value) return
            field = value
            invalidateButtonClickable()
        }

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    init {
        View.inflate(context, R.layout.widget_loading_button, this)
    }

    private fun init(attrs: AttributeSet?) {
        clipToOutline = true

        //region Attributes
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton)
        try {
            minimumHeight =
                typedArray.getDimension(R.styleable.LoadingButton_android_minHeight, tvLoadingButton.minHeight.toFloat()).toInt()

            minimumWidth =
                typedArray.getDimension(R.styleable.LoadingButton_android_minWidth, tvLoadingButton.minWidth.toFloat()).toInt()

            setPadding(
                typedArray.getDimension(R.styleable.LoadingButton_android_paddingStart, 0f).toInt(),
                typedArray.getDimension(R.styleable.LoadingButton_android_paddingTop, 0f).toInt(),
                typedArray.getDimension(R.styleable.LoadingButton_android_paddingEnd, 0f).toInt(),
                typedArray.getDimension(R.styleable.LoadingButton_android_paddingBottom, 0f).toInt()
            )

            insetLeft = typedArray.getDimension(R.styleable.LoadingButton_android_insetLeft, 0f).toInt()
            insetTop = typedArray.getDimension(R.styleable.LoadingButton_android_insetTop, 0f).toInt()
            insetBottom = typedArray.getDimension(R.styleable.LoadingButton_android_insetBottom, 0f).toInt()
            insetRight = typedArray.getDimension(R.styleable.LoadingButton_android_insetRight, 0f).toInt()

            if (typedArray.hasValue(R.styleable.LoadingButton_android_elevation)) {
                elevation = typedArray.getDimension(R.styleable.LoadingButton_android_elevation, 0f)
            }

            if (typedArray.hasValue(R.styleable.LoadingButton_android_stateListAnimator)) {
                stateListAnimator = AnimatorInflater.loadStateListAnimator(
                    context,
                    typedArray.getResourceId(R.styleable.LoadingButton_android_stateListAnimator, 0)
                )
            }

            if (typedArray.hasValue(R.styleable.LoadingButton_android_enabled)) {
                isEnabled = typedArray.getBoolean(R.styleable.LoadingButton_android_enabled, true)
                tvLoadingButton.isEnabled = typedArray.getBoolean(R.styleable.LoadingButton_android_enabled, true)
            }

            if (typedArray.hasValue(R.styleable.LoadingButton_android_textAppearance)) {
                val resourceId = typedArray.getResourceId(R.styleable.LoadingButton_android_textAppearance, 0)
                TextViewCompat.setTextAppearance(tvLoadingButton, resourceId)
            }

            tvLoadingButton.text = typedArray.getString(R.styleable.LoadingButton_android_text)

            tvLoadingButton.isSingleLine = typedArray.getBoolean(R.styleable.LoadingButton_android_singleLine, true)

            tvLoadingButton.maxLines = typedArray.getInt(R.styleable.LoadingButton_android_maxLines, tvLoadingButton.maxLines)

            tvLoadingButton.setTextColor(typedArray.getColorStateList(R.styleable.LoadingButton_android_textColor))

            tvLoadingButton.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                typedArray.getDimension(R.styleable.LoadingButton_android_textSize, tvLoadingButton.textSize)
            )

            tvLoadingButton.isAllCaps = typedArray.getBoolean(R.styleable.LoadingButton_android_textAllCaps, true)

            if (typedArray.hasValue(R.styleable.LoadingButton_lb_background)) {
                background = typedArray.getDrawable(R.styleable.LoadingButton_lb_background)
            }

            if (typedArray.hasValue(R.styleable.LoadingButton_lb_textGravity)) {
                tvLoadingButton.gravity = typedArray.getInt(R.styleable.LoadingButton_lb_textGravity, tvLoadingButton.gravity)
            }

            if (typedArray.hasValue(R.styleable.LoadingButton_lb_progressGravity)) {
                pwLoadingButton.updateLayoutParams<LayoutParams> {
                    gravity = typedArray.getInt(R.styleable.LoadingButton_lb_progressGravity, Gravity.CENTER)
                }
            }

            if (typedArray.hasValue(R.styleable.LoadingButton_strokeColor)) {
                strokeColor =
                    typedArray.getColorStateList(R.styleable.LoadingButton_strokeColor)?.getColorForState(drawableState, 0) ?: 0
            }

            val drawableLeft = typedArray.getDrawable(R.styleable.LoadingButton_android_drawableLeft)
            val drawableStart = typedArray.getDrawable(R.styleable.LoadingButton_android_drawableStart)
            val drawableRight = typedArray.getDrawable(R.styleable.LoadingButton_android_drawableRight)
            val drawableEnd = typedArray.getDrawable(R.styleable.LoadingButton_android_drawableEnd)
            val drawableTop = typedArray.getDrawable(R.styleable.LoadingButton_android_drawableTop)
            val drawableBottom = typedArray.getDrawable(R.styleable.LoadingButton_android_drawableBottom)

            tvLoadingButton.compoundDrawablePadding = typedArray.getDimension(
                R.styleable.LoadingButton_android_drawablePadding,
                tvLoadingButton.compoundDrawablePadding.toFloat()
            ).toInt()
            val isRtlCurrent = tvLoadingButton.layoutDirection == LAYOUT_DIRECTION_RTL
            tvLoadingButton.setCompoundDrawablesWithIntrinsicBounds(
                (if (isRtlCurrent) drawableEnd else drawableStart) ?: drawableLeft,
                drawableTop,
                (if (isRtlCurrent) drawableStart else drawableEnd) ?: drawableRight,
                drawableBottom
            )

            if (typedArray.hasValue(R.styleable.LoadingButton_drawableTint)) {
                val drawables = tvLoadingButton.compoundDrawables
                drawables.forEach {
                    it?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        typedArray.getColor(R.styleable.LoadingButton_drawableTint, ResourcesCompat.getColor(resources, id, context.theme)),
                        BlendModeCompat.SRC_ATOP
                    )
                }
            }

            if (typedArray.hasValue(R.styleable.LoadingButton_lb_progressColor)) {
                pwLoadingButton.setBarColor(typedArray.getColor(R.styleable.LoadingButton_lb_progressColor, 0))
            } else {
                    pwLoadingButton.setBarColor(ResourcesCompat.getColor(resources, id, context.theme))
            }

            if (typedArray.hasValue(R.styleable.LoadingButton_cornerRadius)) {
                cornerRadius =
                    typedArray.getDimension(R.styleable.LoadingButton_cornerRadius, resources.getDimension(R.dimen.corner_4))
            }

            isLoading = typedArray.getBoolean(R.styleable.LoadingButton_lb_isLoading, isLoading)
        } finally {
            typedArray.recycle()
        }
        //endregion

        getMyOutlineProvider()?.let { outlineProvider = it }

        if (isInEditMode && tvLoadingButton.text.isNullOrEmpty()) {
            setText("Button")
        }

        // Default view setting
        if (inAnimation == null && !isInEditMode) {
            inAnimation = AnimationUtils.loadAnimation(context, R.anim.btn_widget_in)
        }

        if (outAnimation == null && !isInEditMode) {
            outAnimation = AnimationUtils.loadAnimation(context, R.anim.btn_widget_out)
        }
    }

    private fun invalidateButtonClickable() {
        super.setOnClickListener(
            if ((!isLoading || isClickableOnLoading) &&
                (onLoadingButtonClickListener != null || innerClickListener != null)) this else null)
        isClickable = !isLoading || isClickableOnLoading
    }

    private fun invalidateMargins() {
        val margins = MarginLayoutParams::class.java.cast(layoutParams)?.apply {
            leftMargin = insetLeft + marginLeft
            topMargin = insetTop + marginTop
            bottomMargin = insetBottom + marginBottom
            rightMargin = insetRight + marginRight
        }
        layoutParams = margins
    }

    override fun setOnClickListener(l: OnClickListener?) {
        innerClickListener = l
        invalidateButtonClickable()
    }

    override fun setStateListAnimator(stateListAnimator: StateListAnimator?) {
        if (isInEditMode) super.setStateListAnimator(null)
        else super.setStateListAnimator(stateListAnimator)
    }

    override fun setInAnimation(inAnimation: Animation?) {
        if (isInEditMode) super.setInAnimation(null)
        else super.setInAnimation(inAnimation)
    }

    override fun setOutAnimation(inAnimation: Animation?) {
        if (isInEditMode) super.setOutAnimation(null)
        else super.setOutAnimation(inAnimation)
    }

    override fun setOutlineProvider(provider: ViewOutlineProvider?) {
        if (isInEditMode) super.setOutlineProvider(null)
        else super.setOutlineProvider(provider)
    }

    private fun getMyOutlineProvider(): ViewOutlineProvider? = object : ViewOutlineProvider() {

        override fun getOutline(view: View, outline: Outline) {
            if (cornerRadius == 0.0F)
                outline.setRect(0, 0, view.width, view.height)
            else
                outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
        }
    }

    fun setTextGravity(gravity: Int){
        tvLoadingButton.gravity = gravity
        tvLoadingButton.requestLayout()
    }

    fun getText(): CharSequence? {
        return tvLoadingButton.text
    }

    fun setText(@StringRes resId: Int) {
        setText(resources.getString(resId))
    }

    fun setText(text: CharSequence) {
        tvLoadingButton.text = text
        tvLoadingButton.requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth - (insetLeft + insetRight)
        val height = measuredHeight - (insetTop + insetBottom)
        setMeasuredDimension(width, height)
    }

    override fun onClick(v: View?) {
        if (v != this) return
        innerClickListener?.onClick(v)
        if (isLoading) {
            onLoadingButtonClickListener?.onLoadingButtonSpinnerClick(this)
        } else {
            onLoadingButtonClickListener?.onLoadingButtonClick(this)
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (strokeColor != 0) {
            canvas?.withSave {
                roundPaint.color = strokeColor
                if (cornerRadius == 0.0f) {
                    this.drawRect(0f, 0f, width.toFloat(), height.toFloat(), roundPaint)
                } else {
                    this.drawRoundRect(
                        0f,
                        0f,
                        width.toFloat(),
                        height.toFloat(),
                        cornerRadius,
                        cornerRadius,
                        roundPaint
                    )
                }
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        invalidateMargins()
        refreshDrawableState()
        jumpDrawablesToCurrentState()
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        (state as? Bundle)?.let {
            super.onRestoreInstanceState(it.getParcelable(EXTRA_KEY_SUPER_STATE))
            val txtState = it.getParcelable<Parcelable>(EXTRA_KEY_TEXT_STATE)
            tvLoadingButton.onRestoreInstanceState(txtState)
            it.getParcelable<Parcelable>(EXTRA_KEY_SPINNER_STATE)
                ?.let { state -> pwLoadingButton.onRestoreInstanceState(state) }
            isClickableOnLoading =
                state.getBoolean(EXTRA_KEY_IS_CLICKABLE_ON_LOADING, isClickableOnLoading)
            animateFirstView = false
            isLoading = state.getBoolean(EXTRA_KEY_IS_LOADING_SHOWN, false)
            return
        }
        // Stops a bug with the wrong state being passed to the super
        super.onRestoreInstanceState(state)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(EXTRA_KEY_SUPER_STATE, super.onSaveInstanceState())
        bundle.putParcelable(EXTRA_KEY_TEXT_STATE, tvLoadingButton.onSaveInstanceState())
        bundle.putParcelable(EXTRA_KEY_SPINNER_STATE, pwLoadingButton.onSaveInstanceState())
        bundle.putBoolean(EXTRA_KEY_IS_LOADING_SHOWN, isLoading)
        bundle.putBoolean(EXTRA_KEY_IS_CLICKABLE_ON_LOADING, isClickableOnLoading)
        return bundle
    }

    interface OnLoadingButtonClickListener {
        fun onLoadingButtonClick(v: LoadingButton)
        fun onLoadingButtonSpinnerClick(v: LoadingButton) {}

        fun OnLoadingButtonClickListener.setOnLoadingButtonClickListenerTo(vararg button: LoadingButton) {
            button.forEach { it.onLoadingButtonClickListener = this }
        }
    }

    companion object {

        const val TITLE_POSITION = 0
        const val LOADING_POSITION = 1

        private const val EXTRA_KEY_SUPER_STATE = "EXTRA_KEY_SUPER_STATE"
        private const val EXTRA_KEY_TEXT_STATE = "EXTRA_KEY_TEXT_STATE"
        private const val EXTRA_KEY_SPINNER_STATE = "EXTRA_KEY_SPINNER_STATE"
        private const val EXTRA_KEY_IS_LOADING_SHOWN = "EXTRA_KEY_IS_LOADING_SHOWN"
        private const val EXTRA_KEY_IS_CLICKABLE_ON_LOADING = "EXTRA_KEY_IS_CLICKABLE_ON_LOADING"
    }
}