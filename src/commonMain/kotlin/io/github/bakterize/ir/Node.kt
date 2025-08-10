package io.github.bakterize.ir

import io.github.bakterize.core.Context
import io.github.bakterize.core.EvalResult
import io.github.bakterize.core.Evaluator
import io.github.bakterize.core.Scalar
import io.github.bakterize.value.Value

sealed class Node(
    open val source: String? = null,
) {
    abstract fun eval(
        ctx: Context,
        evaluator: Evaluator,
    ): EvalResult
}

data class LiteralNode(
    val value: Value,
    override val source: String? = null,
) : Node(source) {
    override fun eval(
        ctx: Context,
        evaluator: Evaluator,
    ) = sequenceOf(Scalar(value))
}

data class IdentifierNode(
    val name: String,
    override val source: String? = null,
) : Node(source) {
    override fun eval(
        ctx: Context,
        evaluator: Evaluator,
    ): EvalResult = ctx.lookup(name)
}
