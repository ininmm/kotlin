class C() {
    operator fun get(i: Int) = i + 1

    // KT-4513: Unnecessary parenthesis around 'this'
    val foo = (this<caret>)[41]
}
