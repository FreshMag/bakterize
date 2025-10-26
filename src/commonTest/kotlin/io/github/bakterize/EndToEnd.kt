package io.github.bakterize

import io.github.bakterize.dsl.Core.asExpr
import io.kotest.common.KotestInternal
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@OptIn(KotestInternal::class)
class EndToEnd : StringSpec({

    "Basic Bakterize evaluation".config(enabled = false) {
        val expression = "1 + 1".asExpr()
        val result = Bakterize.evaluate(expression).firstOrNull()
        result?.castToString()?.value shouldBe "2"
    }

    "No expressions".config(enabled = false) {
        val expression = "no expressions here"
        val result = Bakterize.evaluate(expression).firstOrNull()
        result?.castToString()?.value shouldBe "no expressions here"
    }
})
