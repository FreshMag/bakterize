package io.github.bakterize.ir

import io.github.bakterize.ir.Util.context
import io.github.bakterize.ir.Util.evalNode
import io.github.bakterize.ir.Util.func
import io.github.bakterize.ir.Util.id
import io.github.bakterize.ir.Util.invoke
import io.github.bakterize.ir.Util.literal
import io.github.bakterize.ir.Util.minus
import io.github.bakterize.ir.Util.plus
import io.github.bakterize.ir.Util.times
import io.github.bakterize.util.scalar
import io.github.bakterize.value.Value.Companion.asIntValue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class CallSpec : StringSpec({

    "Simple call of identifier" {
        context(
            "func" to
                listOf(
                    func("x".id(), context(), "x"),
                ),
        ).evalNode(
            "func".id().invoke(42.literal()),
        ).toList() shouldBe
            listOf(
                42.scalar(),
            )
    }

    "Simple call with multiple arguments" {
        context(
            "func" to
                listOf(
                    func("x".id() + "y".id(), context(), "x", "y"),
                ),
        ).evalNode(
            "func".id().invoke(12.literal(), 2.literal()),
        ).toList() shouldBe
            listOf(
                14.scalar(),
            )
    }

    "Call with multiple arguments and cartesian product" {
        val expected =
            listOf(
                12.scalar(),
                8.scalar(),
                20.scalar(),
                22.scalar(),
                18.scalar(),
                40.scalar(),
            )
        context(
            "func" to
                listOf(
                    func("x".id() + "y".id(), context(), "x", "y"),
                    func("x".id() - "y".id(), context(), "x", "y"),
                    func("x".id() * "y".id(), context(), "x", "y"),
                ),
            "a" to listOf(10.asIntValue(), 20.asIntValue()),
        ).evalNode(
            "func".id().invoke("a".id(), 2.literal()),
        ).toList() shouldContainExactlyInAnyOrder expected
    }

    "Function returning another function" {
        context(
            "makeAdder" to
                listOf(
                    func(
                        func("x".id() + 10.literal(), context(), "x").literal(),
                        context(),
                    ),
                ),
        ).evalNode(
            "makeAdder".id().invoke().invoke(3.literal()),
        ).toList() shouldBe
            listOf(
                13.scalar(),
            )
    }

    /**
     * A program like this:
     * ```
     * val makeAdder = [() => {
     *   val x = [10, 20, 30]
     *   return (y) => x + y
     * },
     * () => {
     *   val x = [10, 20, 30]
     *   return (y) => x - y
     * }]
     */
    "Function returning a closure" {
        context(
            "makeAdder" to
                listOf(
                    func(
                        func(
                            "x".id() + "y".id(),
                            context(
                                "x" to listOf(10.asIntValue(), 20.asIntValue(), 30.asIntValue()),
                            ),
                            "y",
                        ).literal(),
                        context(),
                    ),
                    func(
                        func(
                            "x".id() - "y".id(),
                            context(
                                "x" to listOf(10.asIntValue(), 20.asIntValue(), 30.asIntValue()),
                            ),
                            "y",
                        ).literal(),
                        context(),
                    ),
                ),
        ).evalNode(
            "makeAdder".id().invoke().invoke(5.literal()),
        ).toList() shouldContainExactlyInAnyOrder
            listOf(
                15.scalar(),
                25.scalar(),
                35.scalar(),
                5.scalar(),
                15.scalar(),
                25.scalar(),
            )
    }
})
