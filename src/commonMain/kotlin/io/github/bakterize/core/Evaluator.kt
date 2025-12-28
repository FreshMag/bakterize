package io.github.bakterize.core

import io.github.bakterize.ir.Node
import io.github.bakterize.util.cartesianProduct

class Evaluator {
    fun evaluate(
        ctx: Context,
        node: Node,
    ): EvalResult {
        val vars = SymbolVisitor.collectSymbols(node).filterNot { ctx.hasBinding(it) }
        val domains =
            vars.map { v ->
                ctx.findVariable(v) ?: throw IllegalArgumentException("No binding for variable $v")
            }
        return if (domains.isEmpty()) {
            node.evaluateLocally(ctx, this)
        } else {
            cartesianProduct(domains)
                .flatMap { bindings ->
                    val assignedCtx = ctx.copy(bindings = Bindings(bindings.associateBy { it.symbol }))
                    node.evaluateLocally(assignedCtx, this)
                }.run { Cartesian(this) }
        }
    }
}
