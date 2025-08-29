package io.github.bakterize.ir

import io.github.bakterize.core.Cartesian
import io.github.bakterize.core.Context
import io.github.bakterize.core.EvalResult
import io.github.bakterize.core.Evaluator
import io.github.bakterize.util.Operators.apply

enum class BinaryOperatorKind {
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    MODULO,
    CONCAT,
    EQ,
    NE,
    LT,
    LE,
    GT,
    GE,
    AND,
    OR,
}

data class BinaryOperation(
    val left: Node,
    val right: Node,
    val operator: BinaryOperatorKind,
    override val source: String? = null,
) : Node(source) {
    override val children: List<Node>
        get() = listOf(left, right)

    override fun eval(
        ctx: Context,
        evaluator: Evaluator,
    ): EvalResult =
        left
            .eval(ctx, evaluator)
            .flatMap { lv ->
                right.eval(ctx, evaluator).map { rv ->
                    operator.apply(lv, rv)
                }
            }.run { Cartesian(this) }
}
