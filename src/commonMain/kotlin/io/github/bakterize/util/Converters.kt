package io.github.bakterize.util

import io.github.bakterize.core.Scalar

fun Int.scalar() = Scalar.ofInt(this)

fun String.scalar() = Scalar.ofString(this)

fun Boolean.scalar() = Scalar.ofBoolean(this)

fun Double.scalar() = Scalar.ofDouble(this)
