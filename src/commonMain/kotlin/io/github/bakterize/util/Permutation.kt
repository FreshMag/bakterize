package io.github.bakterize.util

internal fun <T> cartesianProduct(sequences: List<Sequence<T>>): Sequence<List<T>> =
    when {
        sequences.isEmpty() -> sequenceOf(emptyList())
        else -> {
            val head = sequences.first()
            val tailProduct = cartesianProduct(sequences.drop(1))
            head.flatMap { h ->
                tailProduct.map { t -> listOf(h) + t }
            }
        }
    }
