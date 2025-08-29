package io.github.bakterize.ir

import io.github.bakterize.core.Context
import io.github.bakterize.core.Evaluator
import io.github.bakterize.core.ListInstance
import io.github.bakterize.core.Scalar
import io.github.bakterize.util.scalar
import io.github.bakterize.value.Value
import io.github.bakterize.value.Value.Companion.asStringValue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class IdentifierSpec : StringSpec({

    fun eval(
        context: Context,
        node: Node,
    ) = Evaluator().evaluate(context, node)

    "Simple identifier" {
        val identifier = IdentifierNode("test")

        val context =
            Context(
                emptyMap(),
            ).withBinding(
                "test",
                sequenceOf(
                    42.scalar(),
                    "test".scalar(),
                ),
            )
        eval(context, identifier).toList() shouldBe
            listOf(
                Scalar(Value.ofInt(42)),
                Scalar(Value.ofString("test")),
            )
    }

    "Identifier in a binary operation" {
        val identifier = IdentifierNode("value")
        val context =
            Context(
                mapOf(
                    "value" to
                        sequenceOf(
                            1.scalar(),
                            2.scalar(),
                            ListInstance(listOf(3.scalar(), 4.scalar())),
                        ),
                ),
            )
        val binaryOperation =
            BinaryOperation(
                left = identifier,
                right = LiteralNode(Value.ofInt(3)),
                operator = BinaryOperatorKind.PLUS,
            )
        eval(context, binaryOperation).toList() shouldBe
            listOf(
                4.scalar(),
                5.scalar(),
                ListInstance(
                    listOf(
                        6.scalar(),
                        7.scalar(),
                    ),
                ),
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
        eval(context, binaryOperation).toList() shouldBe
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
        eval(context, binaryOperation3).toList() shouldBe expected
    }

    "Tied variable" {
        val identifier = IdentifierNode("tied")
        val context =
            Context(
                mapOf(
                    "tied" to sequenceOf(1.scalar(), 2.scalar()),
                ),
            )
        val binaryOperation =
            BinaryOperation(
                left = identifier,
                right = identifier,
                operator = BinaryOperatorKind.PLUS,
            )
        eval(context, binaryOperation).toList() shouldBe
            listOf(
                2.scalar(),
                4.scalar(),
            )
    }

    "Complex expression with tied and free variables" {
        val tied = IdentifierNode("tied")
        val free = IdentifierNode("free")
        val context =
            Context(
                mapOf(
                    "tied" to sequenceOf(1.scalar(), 2.scalar()),
                    "free" to sequenceOf(3.scalar(), 4.scalar()),
                ),
            )
        val binaryOperation1 = // Tied + Free
            BinaryOperation(
                left = tied,
                right = free,
                operator = BinaryOperatorKind.PLUS,
            )
        val binaryOperation2 = // (Tied + Free) * Tied
            BinaryOperation(
                left = binaryOperation1,
                right = tied,
                operator = BinaryOperatorKind.MULTIPLY,
            )
        val binaryOperation3 = // (Tied + Free) * Tied + Free
            BinaryOperation(
                left = binaryOperation2,
                right = free,
                operator = BinaryOperatorKind.PLUS,
            )
        eval(context, binaryOperation3).toSet() shouldBe
            setOf(
                7.scalar(),
                13.scalar(),
                9.scalar(),
                16.scalar(),
            )
    }

    "Expansion of only the symbols used inside the expression" {
        val a = IdentifierNode("A")
        val b = IdentifierNode("B")
        val context =
            Context(
                mapOf(
                    "C" to sequenceOf(5.scalar(), 6.scalar()),
                    "B" to sequenceOf(3.scalar(), 4.scalar()),
                    "A" to sequenceOf(1.scalar(), 2.scalar(), 3.scalar()),
                ),
            )
        val binaryOperation =
            BinaryOperation(
                left = a,
                right = b,
                operator = BinaryOperatorKind.CONCAT,
            )
        eval(context, binaryOperation).toList() shouldBe
            listOf(
                Scalar("13".asStringValue()),
                Scalar("14".asStringValue()),
                Scalar("23".asStringValue()),
                Scalar("24".asStringValue()),
                Scalar("33".asStringValue()),
                Scalar("34".asStringValue()),
            )
    }
})
