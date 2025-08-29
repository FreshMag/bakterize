package io.github.bakterize.core

import io.github.bakterize.ir.IdentifierNode
import io.github.bakterize.ir.Node
import io.github.bakterize.ir.visitor.Visitor

class SymbolVisitor : Visitor<MutableSet<String>, MutableSet<String>>({ _, acc -> acc }) {
    override fun visitIdentifier(
        node: IdentifierNode,
        acc: MutableSet<String>,
    ): MutableSet<String> {
        acc.add(node.name)
        return super.visitIdentifier(node, acc)
    }

    companion object {
        fun collectSymbols(node: Node): Set<String> = SymbolVisitor().visit(node, mutableSetOf()).toSet()
    }
}
