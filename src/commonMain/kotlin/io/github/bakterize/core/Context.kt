package io.github.bakterize.core

sealed class EvalResult(
    var newContext: Context? = null,
) : Sequence<Instance>

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
    private val variables: Variables = Variables(),
    private val bindings: Bindings = Bindings(),
    val parent: Context? = null,
) {
    fun lookup(name: String): Binding? =
        bindings[name]
            ?: parent?.lookup(name)

    fun findVariable(name: Symbol): Variable? =
        variables[name]
            ?: parent?.findVariable(name)

    fun instancesOf(name: Symbol): EvalResult =
        lookup(name)
            ?.let { Single(it.value) }
            ?: findVariable(name)
                ?.let { Cartesian(it.values) }
            ?: Empty()

    fun withVariable(
        name: Symbol,
        multiValue: MultiValue,
    ): Context = copy(variables = variables + (name to Variable(name, multiValue)))

    fun withVariables(vararg newVariables: Pair<Symbol, MultiValue>): Context =
        copy(
            variables =
                Variables(
                    variables +
                        newVariables.associate { (name, values) ->
                            name to Variable(name, values)
                        },
                ),
        )

    fun withAssignment(
        name: Symbol,
        value: Instance,
    ): Context = copy(bindings = bindings + (name to value))

    fun hasBinding(symbol: Symbol): Boolean = lookup(symbol) != null
}
