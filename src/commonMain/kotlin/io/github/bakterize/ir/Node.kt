package io.github.bakterize.ir

import io.github.bakterize.core.Cartesian
import io.github.bakterize.core.Context
import io.github.bakterize.core.EvalResult
import io.github.bakterize.core.Evaluator
import io.github.bakterize.core.Instance
import io.github.bakterize.core.Scalar
import io.github.bakterize.core.Single
import io.github.bakterize.util.cartesianProduct
import io.github.bakterize.value.Type
import io.github.bakterize.value.Value

sealed class Node(
    open val source: String? = null,
) {
    abstract val children: List<Node>

    fun eval(
        ctx: Context,
        evaluator: Evaluator,
    ): EvalResult = evaluator.evaluate(ctx, this)

    abstract fun evaluateLocally(
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

    override fun evaluateLocally(
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

    override fun evaluateLocally(
        ctx: Context,
        evaluator: Evaluator,
    ): EvalResult = ctx.instancesOf(name)
}

data class CallNode(
    val callee: Node,
    val arguments: List<Node>,
    override val source: String? = null,
) : Node(source) {
    override val children: List<Node>
        get() = listOf(callee) + arguments

    @Suppress("UseRequire")
    override fun evaluateLocally(
        ctx: Context,
        evaluator: Evaluator,
    ): EvalResult {
        val calleeResults = evaluator.evaluate(ctx, callee)
        return calleeResults
            .flatMap { calleeInstance ->
                if (calleeInstance !is Scalar || calleeInstance.value.type != Type.FUNCTION) {
                    return@flatMap emptySequence()
                }

                val function = calleeInstance.value.castToFunction()

                val argResults: List<EvalResult> =
                    arguments.map { argNode -> evaluator.evaluate(ctx, argNode) }

                val argCombinations: Sequence<List<Instance>> = cartesianProduct(argResults)
                argCombinations.flatMap { args ->
                    function(
                        evaluator,
                        args.map {
                            if (it !is Scalar) {
                                throw IllegalArgumentException(
                                    "Function arguments must be scalar values.",
                                )
                            }
                            it.value
                        },
                    )
                }
            }.run { Cartesian(this) }
    }
}
