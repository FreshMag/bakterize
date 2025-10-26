package io.github.bakterize.ir.visitor

import io.github.bakterize.ir.BinaryOperation
import io.github.bakterize.ir.CallNode
import io.github.bakterize.ir.DeclarationNode
import io.github.bakterize.ir.IdentifierNode
import io.github.bakterize.ir.LiteralNode
import io.github.bakterize.ir.Node
import io.github.bakterize.ir.StatementNode

abstract class VoidVisitor<T>(
    val default: (Node) -> T,
) {
    fun visitDefault(node: Node): T {
        node.children.forEach { visit(it) }
        return default(node)
    }

    fun visit(node: Node): T =
        when (node) {
            is BinaryOperation -> visitBinaryOperation(node)
            is IdentifierNode -> visitIdentifier(node)
            is LiteralNode -> visitLiteral(node)
            is CallNode -> visitCall(node)
            is StatementNode -> visitStatements(node)
            is DeclarationNode -> visitDeclaration(node)
        }

    open fun visitDeclaration(node: DeclarationNode): T = visitDefault(node)

    open fun visitCall(node: CallNode): T = visitDefault(node)

    open fun visitLiteral(node: LiteralNode): T = visitDefault(node)

    open fun visitIdentifier(node: IdentifierNode): T = visitDefault(node)

    open fun visitBinaryOperation(node: BinaryOperation): T = visitDefault(node)

    open fun visitStatements(node: StatementNode): T = visitDefault(node)
}
