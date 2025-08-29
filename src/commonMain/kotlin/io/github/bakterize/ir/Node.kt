package io.github.bakterize.ir

import io.github.bakterize.core.Context
import io.github.bakterize.core.Empty
import io.github.bakterize.core.EvalResult
import io.github.bakterize.core.Evaluator
import io.github.bakterize.core.Scalar
import io.github.bakterize.core.Single
import io.github.bakterize.value.Value

sealed class Node(
    open val source: String? = null,
) {
    abstract val children: List<Node>

    abstract fun eval(
        ctx: Context,
        evaluator: Evaluator,
    ): EvalResult
}

data class LiteralNode(
    val value: Value,
    override val source: String? = null,
) : Node(source) {
    override val children: List<Node>
        get() = emptyList()

    override fun eval(
        ctx: Context,
        evaluator: Evaluator,
    ) = Single(Scalar(value))
}

data class IdentifierNode(
    val name: String,
    override val source: String? = null,
) : Node(source) {
    override val children: List<Node>
        get() = emptyList()

    override fun eval(
        ctx: Context,
        evaluator: Evaluator,
    ): EvalResult =
        ctx
            .lookup(name)
            ?.let { Single(it) }
            ?: Empty()
}
