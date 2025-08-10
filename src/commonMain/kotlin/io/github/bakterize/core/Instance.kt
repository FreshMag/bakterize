package io.github.bakterize.core

import io.github.bakterize.value.Value

sealed class Instance

data class Scalar(
    val value: Value,
) : Instance()

data class ListInstance(
    val instances: List<Instance>,
) : Instance()
