package io.github.bakterize.util

import io.github.bakterize.core.Instance
import io.github.bakterize.core.Scalar
import io.github.bakterize.ir.BinaryOperatorKind
import io.github.bakterize.value.Value

object Operators {
    fun BinaryOperatorKind.apply(
        left: Instance,
        right: Instance,
    ): Instance =
        when {
            left is Scalar && right is Scalar ->
                Scalar(applyToValue(left.value, right.value))

            else -> error("Unsupported instance types: ${left::class} ${right::class}")
        }
}

fun BinaryOperatorKind.applyToValue(
    left: Value,
    right: Value,
): Value =
    when (this) {
        BinaryOperatorKind.PLUS -> left + right
        BinaryOperatorKind.MINUS -> left - right
        BinaryOperatorKind.MULTIPLY -> left * right
        BinaryOperatorKind.DIVIDE -> left / right
        BinaryOperatorKind.MODULO -> left % right
        BinaryOperatorKind.CONCAT -> left.concat(right)
        BinaryOperatorKind.EQ -> left.eq(right)
        BinaryOperatorKind.NE -> left.ne(right)
        BinaryOperatorKind.LT -> left.le(right)
        BinaryOperatorKind.LE -> left.le(right)
        BinaryOperatorKind.GT -> left.gt(right)
        BinaryOperatorKind.GE -> left.ge(right)
        BinaryOperatorKind.AND -> left.and(right)
        BinaryOperatorKind.OR -> left.or(right)
    }

fun Double.toFixedString(): String = if (this % 1 == 0.0) "${this.toInt()}.0" else this.toString()
