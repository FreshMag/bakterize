package io.github.bakterize.core

import io.github.bakterize.value.Value

sealed class Instance

data class Scalar(
    val value: Value,
) : Instance() {
    companion object {
        fun of(value: Value): Scalar = Scalar(value)

        fun ofInt(value: Int): Scalar = Scalar(Value.ofInt(value))

        fun ofString(value: String): Scalar = Scalar(Value.ofString(value))

        fun ofBoolean(value: Boolean): Scalar = Scalar(Value.ofBoolean(value))

        fun ofDouble(value: Double): Scalar = Scalar(Value.ofDouble(value))
    }
}

typealias MultiValue = Sequence<Instance>
typealias Symbol = String

class Variables(
    map: Map<Symbol, Variable>,
) : Map<Symbol, Variable> by map {
    constructor() : this(emptyMap())

    operator fun plus(pair: Pair<Symbol, Variable>): Variables = Variables(this.toMap() + pair)
}

data class Variable(
    val symbol: Symbol,
    val values: MultiValue,
) : Sequence<Binding> by (values.map { Binding(symbol, it) })

class Bindings(
    map: Map<Symbol, Binding>,
) : Map<Symbol, Binding> by map {
    constructor() : this(emptyMap())

    operator fun plus(pair: Pair<Symbol, Instance>): Bindings = Bindings(this + pair)
}

data class Binding(
    val symbol: Symbol,
    val value: Instance,
)

typealias Materialized = Sequence<Context>
