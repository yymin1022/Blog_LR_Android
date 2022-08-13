package com.yong.blog.util

import io.noties.prism4j.annotations.PrismBundle

@PrismBundle(
    include = ["clike", "java", "c"],
    grammarLocatorClassName = ".PostGrammarLocator"
)
class Prism4j {
}