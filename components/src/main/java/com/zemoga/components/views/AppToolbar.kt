package com.zemoga.components.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.zemoga.components.R
import com.zemoga.components.databinding.AppToolbarBinding

class AppToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {

    val toolbar: Toolbar
    private var toolbarMenuId = -1

    private val padding = resources.getDimensionPixelSize(R.dimen.spacing_normal_600)

    init {
        val layoutParams =
            MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setLayoutParams(layoutParams)

        val binding = AppToolbarBinding.inflate(LayoutInflater.from(context), this, true)
        toolbar = binding.toolbarCustom

        setBackgroundResource(R.color.white)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.AppToolbar)
        val showBackButton = attributes.getBoolean(R.styleable.AppToolbar_showBackButton, false)
        setBackButton(showBackButton)

        styleToolbarWithLightTheme()

        attributes.recycle()
    }

    private fun setPaddingForToolbar() {
        when {
            toolbarMenuId != -1 && toolbar.navigationIcon != null -> toolbar.setPadding(0, 0, 0, 0)
            toolbar.navigationIcon != null -> toolbar.setPadding(0, 0, padding, 0)
            toolbarMenuId != -1 -> toolbar.setPadding(padding, 0, 0, 0)
            else -> toolbar.setPadding(padding, 0, padding, 0)
        }
    }

    fun setToolbarMenu(@MenuRes menuId: Int) {
        toolbar.menu.clear()
        toolbar.inflateMenu(menuId)
        toolbarMenuId = menuId

        setPaddingForToolbar()
    }

    fun setTitle(title: String?) {
        setPaddingForToolbar()

        toolbar.title = title.orEmpty()
    }

    fun setOnMenuItemClickListener(listener: Toolbar.OnMenuItemClickListener) {
        toolbar.setOnMenuItemClickListener(listener)
    }

    fun styleToolbarWithLightTheme() {
        setPaddingForToolbar()
        setBackgroundResource(R.color.white)

        toolbar.setTitleTextAppearance(context, R.style.AppToolbarTitleTextAppearance_Dark)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(context, R.color.blue_dark))

        invalidate()
        requestLayout()
    }

    fun setBackButton(show: Boolean) {
        setPaddingForToolbar()

        toolbar.navigationIcon =
            if (show) ContextCompat.getDrawable(context, R.drawable.ic_arrow_back) else null

        toolbar.navigationIcon?.setTint(
            ContextCompat.getColor(
                context,
                R.color.blue_dark
            )
        )
    }

    fun setBackButtonListener(listener: () -> Unit) {
        toolbar.apply {
            setNavigationOnClickListener {
                listener.invoke()
            }
        }
    }
}
