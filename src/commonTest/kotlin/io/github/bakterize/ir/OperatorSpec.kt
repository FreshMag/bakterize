package io.github.bakterize.ir

import io.github.bakterize.core.Context
import io.github.bakterize.core.Evaluator
import io.github.bakterize.core.Scalar
import io.github.bakterize.value.BooleanValue
import io.github.bakterize.value.FloatValue
import io.github.bakterize.value.IntValue
import io.github.bakterize.value.StringValue
import io.github.bakterize.value.Value
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class OperatorSpec : StringSpec({

    "Basic binary operations" {
        forAll(
            row(BinaryOperatorKind.PLUS, IntValue(3)),
            row(BinaryOperatorKind.MINUS, IntValue(-1)),
            row(BinaryOperatorKind.MULTIPLY, IntValue(2)),
            row(BinaryOperatorKind.DIVIDE, FloatValue(0.5)),
            row(BinaryOperatorKind.MODULO, IntValue(1)),
            row(BinaryOperatorKind.CONCAT, StringValue("12")),
            row(BinaryOperatorKind.EQ, BooleanValue(false)),
            row(BinaryOperatorKind.NE, BooleanValue(true)),
            row(BinaryOperatorKind.GT, BooleanValue(false)),
            row(BinaryOperatorKind.GE, BooleanValue(false)),
            row(BinaryOperatorKind.LT, BooleanValue(true)),
            row(BinaryOperatorKind.LE, BooleanValue(true)),
        ) { operator, result ->
            BinaryOperator(
                left = LiteralNode(Value.ofInt(1), null),
                right = LiteralNode(Value.ofInt(2), null),
                operator = operator,
            ).eval(Context(emptyMap()), Evaluator())
                .toSet()
                .first() shouldBe Scalar(result)
        }
    }
})
