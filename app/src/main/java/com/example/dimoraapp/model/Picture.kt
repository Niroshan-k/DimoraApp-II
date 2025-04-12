package com.example.dimoraapp.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Picture(
    @DrawableRes val drawableResourseId: Int,
    @StringRes val name: Int,
    @StringRes val price: Int
)

data class PictureInfo(
    @DrawableRes val drawableResourseId: Int
)

