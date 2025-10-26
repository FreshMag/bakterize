package io.github.bakterize.value

import io.github.bakterize.core.Context
import io.github.bakterize.core.Evaluator
import io.github.bakterize.core.Instance
import io.github.bakterize.core.Scalar
import io.github.bakterize.ir.Node

class FunctionValue(
    val argsIdentifiers: List<String>,
    val body: Node,
    val originalContext: Context = Context(emptyMap()),
) : Value(Type.FUNCTION) {
    operator fun invoke(
        evaluator: Evaluator,
        args: List<Value>,
    ): Sequence<Instance> {
        if (args.size > argsIdentifiers.size) {
            throw IllegalArgumentException(
                "Too many arguments provided to function." +
                    " Expected at most ${argsIdentifiers.size}, got ${args.size}.",
            )
        }
        val filledContext =
            argsIdentifiers.zip(args).fold(originalContext) { acc, (name, value) ->
                acc.withBinding(name, sequenceOf(Scalar(value)))
            }

        return evaluator.evaluate(filledContext, body)
    }

    override fun cast(targetType: Type): Value {
        if (targetType != Type.FUNCTION) {
            throw UnsupportedOperationException("Cannot cast function value to $targetType.")
        }
        return this
    }

    override fun eq(other: Value): BooleanValue {
        if (other !is FunctionValue) {
            throw UnsupportedOperationException("Cannot compare function value with $other.")
        }
        return BooleanValue(this === other)
    }

    override fun toString(): String =
        "FunctionValue(argsIdentifiers=$argsIdentifiers, body=$body, originalContext=$originalContext)"

    companion object {
        fun withNodeBody(
            argsIdentifiers: List<String>,
            bodyNode: Node,
            originalContext: Context = Context(emptyMap()),
        ): FunctionValue =
            FunctionValue(
                argsIdentifiers,
                bodyNode,
                originalContext,
            )
    }
}
