// IGNORE_BACKEND: JS_IR
// EXPECTED_REACHABLE_NODES: 1116
package foo

class Foo {
    fun bar(param: String): String {
        val local = "K"
        var a = object {
            val b = object {
                val bar = param + local
            }
        }
        return a.b.bar
    }
}

fun box(): String {
    return Foo().bar("O")
}

