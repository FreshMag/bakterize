package io.github.bakterize.dsl

import io.github.bakterize.core.Context
import io.github.bakterize.ir.BinaryOperation
import io.github.bakterize.ir.BinaryOperatorKind
import io.github.bakterize.ir.CallNode
import io.github.bakterize.ir.IdentifierNode
import io.github.bakterize.ir.LiteralNode
import io.github.bakterize.ir.Node
import io.github.bakterize.ir.StatementNode
import io.github.bakterize.value.FunctionValue
import io.github.bakterize.value.Value

object Core {
    fun expr(expression: String): String = "\${{ $expression }}"

    fun String.asExpr(): String = expr(this)

    operator fun Node.plus(other: Node): Node =
        BinaryOperation(
            left = this,
            right = other,
            operator = BinaryOperatorKind.PLUS,
        )

    operator fun Node.minus(other: Node): Node =
        BinaryOperation(
            left = this,
            right = other,
            operator = BinaryOperatorKind.MINUS,
        )

    operator fun Node.times(other: Node): Node =
        BinaryOperation(
            left = this,
            right = other,
            operator = BinaryOperatorKind.MULTIPLY,
        )

    operator fun Node.div(other: Node): Node =
        BinaryOperation(
            left = this,
            right = other,
            operator = BinaryOperatorKind.DIVIDE,
        )

    operator fun Node.rem(other: Node): Node =
        BinaryOperation(
            left = this,
            right = other,
            operator = BinaryOperatorKind.MODULO,
        )

    fun String.id() = IdentifierNode(this)

    fun func(
        node: Node,
        originalContext: Context,
        vararg argsIds: String,
    ) = FunctionValue.withNodeBody(argsIds.toList(), node, originalContext)

    fun Node.invoke(vararg args: Node) = CallNode(this, args.toList())

    fun Any.literal(): LiteralNode =
        when (this) {
            is Value -> LiteralNode(this)
            is Int -> LiteralNode(Value.ofInt(this))
            is String -> LiteralNode(Value.ofString(this))
            is Boolean -> LiteralNode(Value.ofBoolean(this))
            is Double -> LiteralNode(Value.ofDouble(this))
            else -> throw IllegalArgumentException("Unsupported type: ${this.let { it::class }}")
        }

    fun stmt(
        stmt: Node,
        expression: Node,
    ) = StatementNode(stmt, expression)
}
