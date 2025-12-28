package io.github.bakterize.ir

import io.github.bakterize.core.Context
import io.github.bakterize.core.Evaluator
import io.github.bakterize.core.Scalar
import io.github.bakterize.core.Variables
import io.github.bakterize.value.Value

object Util {
    fun eval(
        context: Context,
        node: Node,
    ) = Evaluator().evaluate(context, node)

    fun context(vararg pairs: Pair<String, List<Any>>): Context =
        pairs.asList().fold(Context(Variables())) { acc, (key, value) ->
            acc.withVariable(
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

    infix fun String.bounded(value: Node) = SimpleDeclarationNode(this, value)
}
