package io.github.bakterize.ir

import io.github.bakterize.core.Context
import io.github.bakterize.core.Empty
import io.github.bakterize.core.EvalResult
import io.github.bakterize.core.Evaluator

data class StatementNode(
    val statement: Node,
    val expression: Node,
    override val source: String? = null,
) : Node(source) {
    override val children: List<Node>
        get() = listOf(statement)

    override fun eval(
        ctx: Context,
        evaluator: Evaluator,
    ): EvalResult {
        val stmtResult = statement.eval(ctx, evaluator)
        val newCtx = stmtResult.newContext ?: ctx
        return evaluator.evaluate(newCtx, expression)
    }
}

sealed class DeclarationNode(
    open val name: String,
    open val value: Node,
    val resolution: (Node, Context, Evaluator) -> EvalResult,
    override val source: String? = null,
) : Node(source) {
    override val children: List<Node>
        get() = listOf(value)

    override fun eval(
        ctx: Context,
        evaluator: Evaluator,
    ): EvalResult {
        val newCtx = ctx.withBinding(name, resolution(value, ctx, evaluator))
        return Empty().apply { newContext = newCtx }
    }
}

/**
 * Represents a NON-PERMUTATING declaration of a variable. That is, declaring a variable with this node doesn't free
 * the binding between symbols.
 * E.g.
 * ```
 * let x = [5, 10];
 * val y = x + 10;  // non-permutating declaration of y that is BOUND to x, so doesn't generate higher cardinality
 * x + y            // y doesn't free the binding of x, so this evaluates to [20, 25] and not [20, 25, 20, 25]
 */
data class SimpleDeclarationNode(
    override val name: String,
    override val value: Node,
    override val source: String? = null,
) : DeclarationNode(
        name,
        value,
        { node, ctx, evaluator -> value.eval(ctx, evaluator) },
        source,
    )

/**
 * Represents a PERMUTATING declaration of a variable. That is, declaring a variable with this node FREES
 * the binding between symbols.
 * E.g.
 * ```
 * let x = [5, 10];
 * let y = x + 10;  // permutating declaration of y unbound to x, so generates higher cardinality
 * x + y            // this evaluates to [20, 25, 20, 25]
 */
data class FreeDeclarationNode(
    override val name: String,
    override val value: Node,
    override val source: String? = null,
) : DeclarationNode(
        name,
        value,
        { node, ctx, evaluator -> evaluator.evaluate(ctx, node) },
        source,
    )
