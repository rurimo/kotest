package io.kotest.assertions.print

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.throwable.shouldHaveMessage

class DataClassPrintTest : FunSpec() {
   init {

      test("data class print should format data class") {
         val ship = Ship("HMS Queen Elizabeth", "R08", 65000, "Queen Elizbeth", true)
         DataClassPrintJvm().print(ship, 0).value shouldBe
            """
Ship(
  class         =  "Queen Elizbeth"
  displacement  =  65000L
  leadShip      =  true
  name          =  "HMS Queen Elizabeth"
  pennnant      =  "R08"
)
""".trim()
      }

      test("data class print should format nested data class") {
         val ship = Ship("HMS Queen Elizabeth", "R08", 65000, "Queen Elizbeth", true)
         val shipyard = Shipyard("Rosyth Dockyard", "Fife, Scotland", ship)
         DataClassPrintJvm().print(shipyard, 0).value shouldBe
            """
Shipyard(
  location  =  "Fife, Scotland"
  name      =  "Rosyth Dockyard"
  starship  =  Ship(
    class         =  "Queen Elizbeth"
    displacement  =  65000L
    leadShip      =  true
    name          =  "HMS Queen Elizabeth"
    pennnant      =  "R08"
  )
)
""".trim()
      }

      test("print should default to basic data class output") {
         Ship("HMS Queen Elizabeth", "R08", 65000, "Queen Elizbeth", true).print().value shouldBe
            """Ship(name=HMS Queen Elizabeth, pennnant=R08, displacement=65000, class=Queen Elizbeth, leadShip=true)"""
      }

      test("should be resilient to direct cyclic references") {
         val foo1 = Foo(null)
         val foo2 = Foo(foo1)
         foo1.other = foo2
         foo1.print().value shouldBe """foo"""
      }

      test("should be resilient to indirect cyclic references") {
         val bar = Bar(4, mutableListOf())
         bar.list.add(bar)
         bar.print().value shouldBe """bar"""
      }
   }
}

data class Shipyard(
   val name: String,
   val location: String,
   val starship: Ship,
)

data class Ship(
   val name: String,
   val pennnant: String,
   val displacement: Long,
   val `class`: String,
   val leadShip: Boolean
)

data class Foo(var other: Foo?) {
   override fun hashCode(): Int = 0
   override fun equals(other: Any?): Boolean = false
   override fun toString(): String = "foo"
}

data class Bar(val a: Int, val list: MutableList<Bar>) {
   override fun hashCode(): Int = 0
   override fun equals(other: Any?): Boolean = false
   override fun toString(): String = "bar"
}
