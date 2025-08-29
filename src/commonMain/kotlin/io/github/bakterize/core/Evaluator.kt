package io.github.bakterize.core

import io.github.bakterize.ir.Node
import io.github.bakterize.util.cartesianProduct

class Evaluator {
    fun evaluate(
        ctx: Context,
        node: Node,
    ): EvalResult {
        val vars = SymbolVisitor.collectSymbols(node)
        val domains = vars.map { v -> ctx.bindings[v]!! }

        return cartesianProduct(domains)
            .flatMap { values ->
                val assignment = vars.zip(values).toMap()
                val assignedCtx = ctx.copy(assignment = assignment)
                node.eval(assignedCtx, this)
            }.run { Cartesian(this) }
    }
}
