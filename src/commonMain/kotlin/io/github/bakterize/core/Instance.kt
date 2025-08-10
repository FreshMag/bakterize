package io.github.bakterize.core

import io.github.bakterize.value.Value

sealed class Instance

data class Scalar(
    val value: Value,
) : Instance() {
    companion object {
        fun of(value: Value): Scalar = Scalar(value)

        fun ofInt(value: Int): Scalar = Scalar(Value.ofInt(value))

        fun ofString(value: String): Scalar = Scalar(Value.ofString(value))

        fun ofBoolean(value: Boolean): Scalar = Scalar(Value.ofBoolean(value))

        fun ofDouble(value: Double): Scalar = Scalar(Value.ofDouble(value))
    }
}

data class ListInstance(
    val instances: List<Instance>,
) : Instance()
