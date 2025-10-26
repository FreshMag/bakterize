package io.github.bakterize.value

data class IntValue(
    val value: Int,
) : Value(Type.INTEGER) {
    override fun toString(): String = "int($value)"

    override fun cast(targetType: Type): Value =
        when (targetType) {
            Type.STRING -> StringValue(value.toString())
            Type.INTEGER -> this
            Type.FLOAT -> FloatValue(value.toDouble())
            Type.BOOLEAN -> BooleanValue(value != 0)
            else -> throw UnsupportedOperationException("Cannot cast int value $this to $targetType.")
        }

    override fun eq(other: Value): BooleanValue =
        when (other) {
            is FloatValue -> BooleanValue(value.toDouble() == other.value)
            is IntValue -> BooleanValue(value == other.value)
            is BooleanValue -> BooleanValue(value != 0 == other.value)
            else -> throw UnsupportedOperationException("Cannot compare float value $this with $other.")
        }

    override fun plus(other: Value): Value =
        when (other) {
            is StringValue ->
                other
                    .castOrNull(type)
                    ?.plus(this)
                    ?: StringValue(value.toString() + other.value)
            is IntValue -> IntValue(value + other.value)
            is BooleanValue -> throw UnsupportedOperationException("Cannot add boolean value $other to int: $this")
            is FloatValue -> FloatValue(value.toDouble() + other.value)
            is ObjectValue -> throw UnsupportedOperationException("Cannot add object value $other to int: $this")
            is FunctionValue -> throw UnsupportedOperationException("Cannot add function value $other to int: $this")
        }

    override fun minus(other: Value): Value =
        when (other) {
            is StringValue ->
                other
                    .castOrNull(type)
                    ?.let {
                        this - it
                    }
                    ?: throw UnsupportedOperationException("Cannot subtract $other from $this")
            is IntValue -> IntValue(value - other.value)
            is BooleanValue -> throw UnsupportedOperationException("Cannot subtract boolean value from $this")
            is FloatValue -> FloatValue(value.toDouble() - other.value)
            is ObjectValue -> throw UnsupportedOperationException("Cannot subtract object value $other from $this")
            is FunctionValue -> throw UnsupportedOperationException("Cannot subtract function value $other from $this")
        }

    override fun times(other: Value): Value =
        when (other) {
            is StringValue ->
                other
                    .castOrNull(type)
                    ?.times(this)
                    ?: throw UnsupportedOperationException("Cannot multiply $other by int value $this")
            is IntValue -> IntValue(value * other.value)
            is BooleanValue -> throw UnsupportedOperationException("Cannot multiply boolean value $other with $this")
            is FloatValue -> FloatValue(value.toDouble() * other.value)
            is ObjectValue -> throw UnsupportedOperationException("Cannot multiply object value $other with int: $this")
            is FunctionValue -> throw UnsupportedOperationException(
                "Cannot multiply function value $other with int: $this",
            )
        }

    override fun div(other: Value): Value =
        when (other) {
            is StringValue ->
                other
                    .castOrNull(type)
                    ?.let {
                        this / it
                    }
                    ?: throw UnsupportedOperationException("Cannot divide $this by $other")
            is IntValue ->
                if (other.value == 0) {
                    throw ArithmeticException("Division by zero")
                } else {
                    FloatValue(value.toDouble() / other.value)
                }
            is BooleanValue -> throw UnsupportedOperationException("Cannot divide boolean value by $this")
            is FloatValue -> FloatValue(value.toDouble() / other.value)
            is ObjectValue -> throw UnsupportedOperationException("Cannot divide object value $other by int: $this")
            is FunctionValue -> throw UnsupportedOperationException("Cannot divide function value $other by int: $this")
        }

    override fun rem(other: Value): Value =
        when (other) {
            is StringValue ->
                other
                    .castOrNull(type)
                    ?.let {
                        this % it
                    }
                    ?: throw UnsupportedOperationException("Cannot modulo int value $this by $other")
            is IntValue ->
                if (other.value == 0) {
                    throw ArithmeticException("Division by zero")
                } else {
                    IntValue(value % other.value)
                }
            is BooleanValue -> throw UnsupportedOperationException("Cannot modulo boolean value $other with $this")
            is FloatValue -> FloatValue(value.toDouble() % other.value)
            is ObjectValue -> throw UnsupportedOperationException("Cannot modulo object value $other with int: $this")
            is FunctionValue -> throw UnsupportedOperationException(
                "Cannot modulo function value $other with int: $this",
            )
        }

    override fun unaryMinus(): Value = IntValue(-value)

    override fun compareTo(other: Value): Int =
        when (other) {
            is IntValue -> value.compareTo(other.value)
            is FloatValue -> value.compareTo(other.value.toInt())
            is StringValue -> throw UnsupportedOperationException("Cannot compare int value $this with string $other")
            is BooleanValue -> throw UnsupportedOperationException("Cannot compare int value $this with boolean $other")
            is ObjectValue -> throw UnsupportedOperationException("Cannot compare int value $this with object $other")
            is FunctionValue -> throw UnsupportedOperationException(
                "Cannot compare function value $this with object $other",
            )
        }
}
