package io.github.bakterize.dsl

object Core {
    fun expr(expression: String): String = "\${{ $expression }}"

    fun String.asExpr(): String = expr(this)
}
