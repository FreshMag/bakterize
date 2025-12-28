package io.github.bakterize.ir

import io.github.bakterize.dsl.Core.id
import io.github.bakterize.dsl.Core.literal
import io.github.bakterize.dsl.Core.minus
import io.github.bakterize.dsl.Core.plus
import io.github.bakterize.dsl.Core.stmt
import io.github.bakterize.ir.Util.bounded
import io.github.bakterize.ir.Util.context
import io.github.bakterize.ir.Util.evalNode
import io.github.bakterize.util.scalar
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class StatementDeclarationSpec : StringSpec({

    "Simple statement with declaration" {
        context()
            .evalNode(
                stmt(
                    "x" bounded 5.literal(),
                    "x".id() + 10.literal(),
                ),
            ).toList() shouldBe
            listOf(
                15.scalar(),
            )
    }

    "Double statement with 2 declarations" {
        context()
            .evalNode(
                stmt(
                    "x" bounded 5.literal(),
                    stmt(
                        "y" bounded 20.literal(),
                        "x".id() + "y".id(),
                    ),
                ),
            ).toList() shouldContainExactlyInAnyOrder
            listOf(
                25.scalar(),
            )
    }

    /**
     * Corresponds to the following pseudocode:
     * ```
     * let z = [100, 200, 300]
     * val x = z + 10
     * val y = 20
     * return x - y
     * ```
     */
    "Double statement with 2 declarations and pre-existing context" {
        context(
            "z" to listOf(100, 200, 300),
        ).evalNode(
            stmt(
                "x" bounded ("z".id() + 10.literal()),
                stmt(
                    "y" bounded (20.literal()),
                    "x".id() - "y".id(),
                ),
            ),
        ).toList() shouldContainExactlyInAnyOrder
            listOf(
                90.scalar(),
                190.scalar(),
                290.scalar(),
            )
    }
})
