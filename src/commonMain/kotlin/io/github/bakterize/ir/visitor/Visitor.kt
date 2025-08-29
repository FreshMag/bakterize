package io.github.bakterize.ir.visitor

import io.github.bakterize.ir.BinaryOperation
import io.github.bakterize.ir.IdentifierNode
import io.github.bakterize.ir.LiteralNode
import io.github.bakterize.ir.Node

abstract class Visitor<T, R>(
    val default: (Node, R) -> T,
) {
    fun visitDefault(
        node: Node,
        acc: R,
    ): T {
        node.children.forEach { visit(it, acc) }
        return default(node, acc)
    }

    fun visit(
        node: Node,
        acc: R,
    ): T =
        when (node) {
            is BinaryOperation -> visitBinaryOperation(node, acc)
            is IdentifierNode -> visitIdentifier(node, acc)
            is LiteralNode -> visitLiteral(node, acc)
        }

    open fun visitLiteral(
        node: LiteralNode,
        acc: R,
    ): T = visitDefault(node, acc)

    open fun visitIdentifier(
        node: IdentifierNode,
        acc: R,
    ): T = visitDefault(node, acc)

    open fun visitBinaryOperation(
        node: BinaryOperation,
        acc: R,
    ): T = visitDefault(node, acc)
}
