package io.github.bakterize.ir

import io.github.bakterize.core.Context
import io.github.bakterize.core.Evaluator
import io.github.bakterize.core.Scalar
import io.github.bakterize.util.scalar
import io.github.bakterize.value.Value
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class IdentifierSpec : StringSpec({

    "Simple identifier" {
        val identifier = IdentifierNode("test")

        val context =
            Context(
                emptyMap(),
            ).withBinding(
                "test",
                sequenceOf(
                    Scalar(
                        Value.ofInt(42),
                    ),
                    Scalar(
                        Value.ofString("test"),
                    ),
                ),
            )
        identifier.eval(context, Evaluator()).toList() shouldBe
            listOf(
                Scalar(Value.ofInt(42)),
                Scalar(Value.ofString("test")),
            )
    }

    "Identifier in a binary operation" {
        val identifier = IdentifierNode("value")
        val context =
            Context(
                mapOf("value" to sequenceOf(1.scalar(), 2.scalar())),
            )
        val binaryOperation =
            BinaryOperation(
                left = identifier,
                right = LiteralNode(Value.ofInt(3)),
                operator = BinaryOperatorKind.PLUS,
            )
        binaryOperation.eval(context, Evaluator()).toList() shouldBe
            listOf(
                Scalar(Value.ofInt(4)),
                Scalar(Value.ofInt(5)),
            )
    }

    "Simple combinatorial expansion" {
        val identifier1 = IdentifierNode("A")
        val identifier2 = IdentifierNode("B")
        val context =
            Context(
                mapOf(
                    "A" to sequenceOf(1.scalar(), 2.scalar()),
                    "B" to sequenceOf(3.scalar(), 4.scalar()),
                ),
            )
        val binaryOperation =
            BinaryOperation(
                left = identifier1,
                right = identifier2,
                operator = BinaryOperatorKind.MULTIPLY,
            )
        binaryOperation.eval(context, Evaluator()).toList() shouldBe
            listOf(
                Scalar(Value.ofInt(3)),
                Scalar(Value.ofInt(4)),
                Scalar(Value.ofInt(6)),
                Scalar(Value.ofInt(8)),
            )
    }

    "Complex combinatorial expansion" {
        val a = IdentifierNode("A")
        val b = IdentifierNode("B")
        val c = IdentifierNode("C")
        val d = IdentifierNode("D")
        val context =
            Context(
                mapOf(
                    "A" to sequenceOf(1.scalar(), 2.scalar(), 3.scalar()),
                    "B" to sequenceOf(3.scalar(), 4.scalar()),
                    "C" to sequenceOf(5.scalar(), 6.scalar()),
                    "D" to sequenceOf(7.scalar(), 8.scalar()),
                ),
            )
        val binaryOperation1 =
            BinaryOperation(
                left = a,
                right = b,
                operator = BinaryOperatorKind.CONCAT,
            )
        val binaryOperation2 =
            BinaryOperation(
                left = c,
                right = d,
                operator = BinaryOperatorKind.PLUS,
            )
        val binaryOperation3 =
            BinaryOperation(
                left = binaryOperation1,
                right = binaryOperation2,
                operator = BinaryOperatorKind.MULTIPLY,
            )
        val expected =
            listOf(1, 2, 3).flatMap { aValue ->
                listOf(3, 4).flatMap { bValue ->
                    listOf(5, 6).flatMap { cValue ->
                        listOf(7, 8).map { dValue ->
                            Scalar(Value.ofInt(("$aValue$bValue".toInt()) * (cValue + dValue)))
                        }
                    }
                }
            }
        binaryOperation3.eval(context, Evaluator()).toList() shouldBe expected
    }
})
