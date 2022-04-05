package com.template

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize

data class URLValue(

    var url: String
): Parcelable
