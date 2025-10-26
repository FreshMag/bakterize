package io.github.bakterize.ir

import io.github.bakterize.core.Context
import io.github.bakterize.core.Evaluator
import io.github.bakterize.core.Scalar
import io.github.bakterize.value.FunctionValue
import io.github.bakterize.value.Value

object Util {
    fun eval(
        context: Context,
        node: Node,
    ) = Evaluator().evaluate(context, node)

    fun context(vararg pairs: Pair<String, List<Any>>): Context =
        pairs.asList().fold(Context(emptyMap())) { acc, (key, value) ->
            acc.withBinding(
                key,
                value
                    .map {
                        when (it) {
                            is Value -> Scalar.of(it)
                            is Int -> Scalar.ofInt(it)
                            is String -> Scalar.ofString(it)
                            is Boolean -> Scalar.ofBoolean(it)
                            is Double -> Scalar.ofDouble(it)
                            else -> throw IllegalArgumentException("Unsupported type: ${it.let { it::class }}")
                        }
                    }.asSequence(),
            )
        }

    fun Context.evalNode(node: Node) = Evaluator().evaluate(this, node)

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

    infix fun String.bounded(value: Node) = SimpleDeclarationNode(this, value)

    infix fun String.unbounded(value: Node) = FreeDeclarationNode(this, value)
}
