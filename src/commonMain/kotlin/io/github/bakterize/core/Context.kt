package io.github.bakterize.core

sealed class EvalResult : Sequence<Instance>

data class Single(
    val instance: Instance,
) : EvalResult() {
    override fun iterator(): Iterator<Instance> = sequenceOf(instance).iterator()
}

data class Cartesian(
    val instances: Sequence<Instance>,
) : EvalResult() {
    override fun iterator(): Iterator<Instance> = instances.iterator()
}

class Empty : EvalResult() {
    override fun iterator(): Iterator<Instance> = emptySequence<Instance>().iterator()
}

data class Context(
    val bindings: Map<String, Sequence<Instance>>,
    val assignment: Map<String, Instance> = emptyMap(),
    val parent: Context? = null,
) {
    fun lookup(name: String): Instance? =
        assignment[name]
            ?: parent?.lookup(name)

    fun withBinding(
        name: String,
        valueSeq: Sequence<Instance>,
    ): Context = copy(bindings = bindings + (name to valueSeq))
}
