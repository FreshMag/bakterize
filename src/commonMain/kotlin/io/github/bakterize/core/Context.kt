package io.github.bakterize.core

typealias EvalResult = Sequence<Instance>

data class Context(
    val bindings: Map<String, EvalResult>,
    val parent: Context? = null,
) {
    fun lookup(name: String): EvalResult = bindings[name] ?: parent?.lookup(name) ?: emptySequence()

    fun withBinding(
        name: String,
        valueSeq: EvalResult,
    ): Context = copy(bindings = bindings + (name to valueSeq))
}
