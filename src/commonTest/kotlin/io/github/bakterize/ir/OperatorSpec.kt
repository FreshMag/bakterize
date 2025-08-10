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
import io.kotest.data.Row2
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class OperatorSpec : StringSpec({

    val comparisons: List<Row2<BinaryOperatorKind, Value>> =
        listOf(
            row(BinaryOperatorKind.EQ, BooleanValue(false)),
            row(BinaryOperatorKind.NE, BooleanValue(true)),
            row(BinaryOperatorKind.GT, BooleanValue(false)),
            row(BinaryOperatorKind.GE, BooleanValue(false)),
            row(BinaryOperatorKind.LT, BooleanValue(true)),
            row(BinaryOperatorKind.LE, BooleanValue(true)),
        )

    "Basic binary operations for integers" {
        forAll(
            row(BinaryOperatorKind.PLUS, IntValue(3)),
            row(BinaryOperatorKind.MINUS, IntValue(-1)),
            row(BinaryOperatorKind.MULTIPLY, IntValue(2)),
            row(BinaryOperatorKind.DIVIDE, FloatValue(0.5)),
            row(BinaryOperatorKind.MODULO, IntValue(1)),
            row(BinaryOperatorKind.CONCAT, StringValue("12")),
            *comparisons.toTypedArray(),
            testfn = { operator: BinaryOperatorKind, result: Value ->
                BinaryOperation(
                    left = LiteralNode(Value.ofInt(1), null),
                    right = LiteralNode(Value.ofInt(2), null),
                    operator = operator,
                ).eval(Context(emptyMap()), Evaluator())
                    .toSet()
                    .first() shouldBe Scalar(result)
            },
        )
    }

    "Basic binary operations for floats" {
        forAll(
            row(BinaryOperatorKind.PLUS, FloatValue(3.0)),
            row(BinaryOperatorKind.MINUS, FloatValue(-1.0)),
            row(BinaryOperatorKind.MULTIPLY, FloatValue(2.0)),
            row(BinaryOperatorKind.DIVIDE, FloatValue(0.5)),
            row(BinaryOperatorKind.MODULO, FloatValue(1.0)),
            row(BinaryOperatorKind.CONCAT, StringValue("1.02.0")),
            *comparisons.toTypedArray(),
        ) { operator, result ->
            BinaryOperation(
                left = LiteralNode(Value.ofDouble(1.0), null),
                right = LiteralNode(Value.ofDouble(2.0), null),
                operator = operator,
            ).eval(Context(emptyMap()), Evaluator())
                .toSet()
                .first() shouldBe Scalar(result)
        }
    }

    "Basic binary operations for strings" {
        forAll(
            row(BinaryOperatorKind.PLUS, StringValue("12")),
            row(BinaryOperatorKind.CONCAT, StringValue("12")),
            *comparisons.toTypedArray(),
        ) { operator, result ->
            BinaryOperation(
                left = LiteralNode(Value.ofString("1"), null),
                right = LiteralNode(Value.ofString("2"), null),
                operator = operator,
            ).eval(Context(emptyMap()), Evaluator())
                .toSet()
                .first() shouldBe Scalar(result)
        }
    }

    "Basic binary operations for booleans" {
        forAll(
            row(BinaryOperatorKind.EQ, BooleanValue(false)),
            row(BinaryOperatorKind.NE, BooleanValue(true)),
            row(BinaryOperatorKind.OR, BooleanValue(true)),
            row(BinaryOperatorKind.AND, BooleanValue(false)),
        ) { operator, result ->
            BinaryOperation(
                left = LiteralNode(Value.ofBoolean(true), null),
                right = LiteralNode(Value.ofBoolean(false), null),
                operator = operator,
            ).eval(Context(emptyMap()), Evaluator())
                .toSet()
                .first() shouldBe Scalar(result)
        }
    }
})
