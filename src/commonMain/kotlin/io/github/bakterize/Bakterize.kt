package io.github.bakterize

import io.github.bakterize.value.Value

object Bakterize {
    fun evaluate(expression: String): Sequence<Value> {
        // This is a placeholder for the actual evaluation logic.
        // In a real implementation, this would parse the expression and evaluate it.
        throw UnsupportedOperationException(
            "Expression evaluation is not implemented yet. " +
                "Cannot evaluate: $expression",
        )
    }
}
