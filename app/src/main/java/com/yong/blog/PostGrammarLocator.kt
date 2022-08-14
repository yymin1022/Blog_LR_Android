package com.yong.blog

import androidx.annotation.Nullable
import io.noties.prism4j.GrammarLocator
import io.noties.prism4j.GrammarUtils
import io.noties.prism4j.Prism4j
import io.noties.prism4j.Prism4j.*
import io.noties.prism4j.annotations.Aliases
import io.noties.prism4j.annotations.Extend
import io.noties.prism4j.annotations.Modify
import java.util.regex.Pattern.CASE_INSENSITIVE
import java.util.regex.Pattern.MULTILINE
import java.util.regex.Pattern.compile

class PostGrammarLocator: GrammarLocator {
    @Nullable
    override fun grammar(prism4j: Prism4j, language: String): Grammar? {
        return when (language) {
            "json" -> Prism_json.create(prism4j)
            "c" -> Prism_c.create(prism4j)
            "c++" -> Prism_cpp.create(prism4j)
            "clike" -> Prism_clike.create(prism4j)
            "java" -> Prism_java.create(prism4j)
            "js" -> Prism_javascript.create(prism4j)
            "py" -> Prism_python.create(prism4j)
            else -> null
        }
    }

    override fun languages(): MutableSet<String> {
        TODO("Not yet implemented")
    }
}

@Aliases("jsonp")
object Prism_json {
    fun create(prism4j: Prism4j): Prism4j.Grammar {
        return grammar(
            "json",
            token(
                "property",
                pattern(compile("\"(?:\\\\.|[^\\\\\"\\r\\n])*\"(?=\\s*:)", CASE_INSENSITIVE))
            ),
            token(
                "string",
                pattern(compile("\"(?:\\\\.|[^\\\\\"\\r\\n])*\"(?!\\s*:)"), false, true)
            ),
            token(
                "number",
                pattern(compile("\\b0x[\\dA-Fa-f]+\\b|(?:\\b\\d+\\.?\\d*|\\B\\.\\d+)(?:[Ee][+-]?\\d+)?"))
            ),
            token("punctuation", pattern(compile("[{}\\[\\]);,]"))),
            token("operator", pattern(compile(":"))),
            token("boolean", pattern(compile("\\b(?:true|false)\\b", CASE_INSENSITIVE))),
            token("null", pattern(compile("\\bnull\\b", CASE_INSENSITIVE)))
        )
    }
}

@Extend("clike")
object Prism_c {
    fun create(prism4j: Prism4j): Grammar {
        val c = GrammarUtils.extend(
            GrammarUtils.require(prism4j, "clike"),
            "c",
            { token ->
                val name = token.name()
                "class-name" != name && "boolean" != name
            },
            token(
                "keyword",
                pattern(compile("\\b(?:_Alignas|_Alignof|_Atomic|_Bool|_Complex|_Generic|_Imaginary|_Noreturn|_Static_assert|_Thread_local|asm|typeof|inline|auto|break|case|char|const|continue|default|do|double|else|enum|extern|float|for|goto|if|int|long|register|return|short|signed|sizeof|static|struct|switch|typedef|union|unsigned|void|volatile|while)\\b"))
            ),
            token(
                "operator",
                pattern(compile("-[>-]?|\\+\\+?|!=?|<<?=?|>>?=?|==?|&&?|\\|\\|?|[~^%?*\\/]"))
            ),
            token(
                "number",
                pattern(
                    compile(
                        "(?:\\b0x[\\da-f]+|(?:\\b\\d+\\.?\\d*|\\B\\.\\d+)(?:e[+-]?\\d+)?)[ful]*",
                        CASE_INSENSITIVE
                    )
                )
            )
        )
        GrammarUtils.insertBeforeToken(
            c, "string",
            token(
                "macro", pattern(
                    compile(
                        "(^\\s*)#\\s*[a-z]+(?:[^\\r\\n\\\\]|\\\\(?:\\r\\n|[\\s\\S]))*",
                        CASE_INSENSITIVE or MULTILINE
                    ),
                    true,
                    false,
                    "property",
                    grammar(
                        "inside",
                        token(
                            "string",
                            pattern(
                                compile("(#\\s*include\\s*)(?:<.+?>|(\"|')(?:\\\\?.)+?\\2)"),
                                true
                            )
                        ),
                        token(
                            "directive", pattern(
                                compile("(#\\s*)\\b(?:define|defined|elif|else|endif|error|ifdef|ifndef|if|import|include|line|pragma|undef|using)\\b"),
                                true,
                                false,
                                "keyword"
                            )
                        )
                    )
                )
            ),
            token(
                "constant",
                pattern(compile("\\b(?:__FILE__|__LINE__|__DATE__|__TIME__|__TIMESTAMP__|__func__|EOF|NULL|SEEK_CUR|SEEK_END|SEEK_SET|stdin|stdout|stderr)\\b"))
            )
        )
        return c
    }
}

object Prism_clike {
    fun create(prism4j: Prism4j): Grammar {
        return grammar(
            "clike",
            token(
                "comment",
                pattern(compile("(^|[^\\\\])\\/\\*[\\s\\S]*?(?:\\*\\/|$)"), true),
                pattern(compile("(^|[^\\\\:])\\/\\/.*"), true, true)
            ),
            token(
                "string",
                pattern(
                    compile("([\"'])(?:\\\\(?:\\r\\n|[\\s\\S])|(?!\\1)[^\\\\\\r\\n])*\\1"),
                    false,
                    true
                )
            ),
            token(
                "class-name",
                pattern(
                    compile("((?:\\b(?:class|interface|extends|implements|trait|instanceof|new)\\s+)|(?:catch\\s+\\())[\\w.\\\\]+"),
                    true,
                    false,
                    null,
                    grammar("inside", token("punctuation", pattern(compile("[.\\\\]"))))
                )
            ),
            token(
                "keyword",
                pattern(compile("\\b(?:if|else|while|do|for|return|in|instanceof|function|new|try|throw|catch|finally|null|break|continue)\\b"))
            ),
            token("boolean", pattern(compile("\\b(?:true|false)\\b"))),
            token("function", pattern(compile("[a-z0-9_]+(?=\\()", CASE_INSENSITIVE))),
            token(
                "number",
                pattern(
                    compile(
                        "\\b0x[\\da-f]+\\b|(?:\\b\\d+\\.?\\d*|\\B\\.\\d+)(?:e[+-]?\\d+)?",
                        CASE_INSENSITIVE
                    )
                )
            ),
            token(
                "operator",
                pattern(compile("--?|\\+\\+?|!=?=?|<=?|>=?|==?=?|&&?|\\|\\|?|\\?|\\*|\\/|~|\\^|%"))
            ),
            token("punctuation", pattern(compile("[{}\\[\\];(),.:]")))
        )
    }
}

@Extend("c")
object Prism_cpp {
    fun create(prism4j: Prism4j): Grammar {
        val cpp = GrammarUtils.extend(
            GrammarUtils.require(prism4j, "c"),
            "cpp",
            token(
                "keyword",
                pattern(compile("\\b(?:alignas|alignof|asm|auto|bool|break|case|catch|char|char16_t|char32_t|class|compl|const|constexpr|const_cast|continue|decltype|default|delete|do|double|dynamic_cast|else|enum|explicit|export|extern|float|for|friend|goto|if|inline|int|int8_t|int16_t|int32_t|int64_t|uint8_t|uint16_t|uint32_t|uint64_t|long|mutable|namespace|new|noexcept|nullptr|operator|private|protected|public|register|reinterpret_cast|return|short|signed|sizeof|static|static_assert|static_cast|struct|switch|template|this|thread_local|throw|try|typedef|typeid|typename|union|unsigned|using|virtual|void|volatile|wchar_t|while)\\b"))
            ),
            token(
                "operator",
                pattern(compile("--?|\\+\\+?|!=?|<{1,2}=?|>{1,2}=?|->|:{1,2}|={1,2}|\\^|~|%|&{1,2}|\\|\\|?|\\?|\\*|\\/|\\b(?:and|and_eq|bitand|bitor|not|not_eq|or|or_eq|xor|xor_eq)\\b"))
            )
        )

        // in prism-js cpp is extending c, but c has not booleans... (like classes)
        GrammarUtils.insertBeforeToken(
            cpp, "function",
            token("boolean", pattern(compile("\\b(?:true|false)\\b")))
        )
        GrammarUtils.insertBeforeToken(
            cpp, "keyword",
            token("class-name", pattern(compile("(class\\s+)\\w+", CASE_INSENSITIVE), true))
        )
        GrammarUtils.insertBeforeToken(
            cpp, "string",
            token(
                "raw-string",
                pattern(
                    compile("R\"([^()\\\\ ]{0,16})\\([\\s\\S]*?\\)\\1\""),
                    false,
                    true,
                    "string"
                )
            )
        )
        return cpp
    }
}

@Extend("clike")
object Prism_java {
    fun create(prism4j: Prism4j): Grammar {
        val keyword = token(
            "keyword",
            pattern(compile("\\b(?:abstract|continue|for|new|switch|assert|default|goto|package|synchronized|boolean|do|if|private|this|break|double|implements|protected|throw|byte|else|import|public|throws|case|enum|instanceof|return|transient|catch|extends|int|short|try|char|final|interface|static|void|class|finally|long|strictfp|volatile|const|float|native|super|while)\\b"))
        )
        val java = GrammarUtils.extend(
            GrammarUtils.require(prism4j, "clike"), "java",
            keyword,
            token(
                "number",
                pattern(
                    compile(
                        "\\b0b[01]+\\b|\\b0x[\\da-f]*\\.?[\\da-fp-]+\\b|(?:\\b\\d+\\.?\\d*|\\B\\.\\d+)(?:e[+-]?\\d+)?[df]?",
                        CASE_INSENSITIVE
                    )
                )
            ),
            token(
                "operator", pattern(
                    compile(
                        "(^|[^.])(?:\\+[+=]?|-[-=]?|!=?|<<?=?|>>?>?=?|==?|&[&=]?|\\|[|=]?|\\*=?|\\/=?|%=?|\\^=?|[?:~])",
                        MULTILINE
                    ),
                    true
                )
            )
        )
        GrammarUtils.insertBeforeToken(
            java, "function",
            token(
                "annotation", pattern(
                    compile("(^|[^.])@\\w+"),
                    true,
                    false,
                    "punctuation"
                )
            )
        )
        GrammarUtils.insertBeforeToken(
            java, "class-name",
            token(
                "generics", pattern(
                    compile(
                        "<\\s*\\w+(?:\\.\\w+)?(?:\\s*,\\s*\\w+(?:\\.\\w+)?)*>",
                        CASE_INSENSITIVE
                    ),
                    false,
                    false,
                    "function",
                    grammar(
                        "inside",
                        keyword,
                        token("punctuation", pattern(compile("[<>(),.:]")))
                    )
                )
            )
        )
        return java
    }
}

@Aliases("js")
@Modify("markup")
@Extend("clike")
object Prism_javascript {
    fun create(prism4j: Prism4j): Grammar {
        val js = GrammarUtils.extend(
            GrammarUtils.require(prism4j, "clike"), "javascript",
            token(
                "keyword",
                pattern(compile("\\b(?:as|async|await|break|case|catch|class|const|continue|debugger|default|delete|do|else|enum|export|extends|finally|for|from|function|get|if|implements|import|in|instanceof|interface|let|new|null|of|package|private|protected|public|return|set|static|super|switch|this|throw|try|typeof|var|void|while|with|yield)\\b"))
            ),
            token(
                "number",
                pattern(compile("\\b(?:0[xX][\\dA-Fa-f]+|0[bB][01]+|0[oO][0-7]+|NaN|Infinity)\\b|(?:\\b\\d+\\.?\\d*|\\B\\.\\d+)(?:[Ee][+-]?\\d+)?"))
            ),
            token(
                "function",
                pattern(
                    compile(
                        "[_\$a-z\\xA0-\\uFFFF][$\\w\\xA0-\\uFFFF]*(?=\\s*\\()",
                        CASE_INSENSITIVE
                    )
                )
            ),
            token(
                "operator",
                pattern(compile("-[-=]?|\\+[+=]?|!=?=?|<<?=?|>>?>?=?|=(?:==?|>)?|&[&=]?|\\|[|=]?|\\*\\*?=?|\\/=?|~|\\^=?|%=?|\\?|\\.{3}"))
            )
        )
        GrammarUtils.insertBeforeToken(
            js, "keyword",
            token(
                "regex", pattern(
                    compile("((?:^|[^$\\w\\xA0-\\uFFFF.\"'\\])\\s])\\s*)\\/(\\[[^\\]\\r\\n]+]|\\\\.|[^/\\\\\\[\\r\\n])+\\/[gimyu]{0,5}(?=\\s*($|[\\r\\n,.;})\\]]))"),
                    true,
                    true
                )
            ),
            token(
                "function-variable",
                pattern(
                    compile(
                        "[_\$a-z\\xA0-\\uFFFF][$\\w\\xA0-\\uFFFF]*(?=\\s*=\\s*(?:function\\b|(?:\\([^()]*\\)|[_\$a-z\\xA0-\\uFFFF][$\\w\\xA0-\\uFFFF]*)\\s*=>))",
                        CASE_INSENSITIVE
                    ),
                    false,
                    false,
                    "function"
                )
            ),
            token("constant", pattern(compile("\\b[A-Z][A-Z\\d_]*\\b")))
        )
        val interpolation = token("interpolation")
        GrammarUtils.insertBeforeToken(
            js, "string",
            token(
                "template-string",
                pattern(
                    compile("`(?:\\\\[\\s\\S]|\\$\\{[^}]+\\}|[^\\\\`])*`"),
                    false,
                    true,
                    null,
                    grammar(
                        "inside",
                        interpolation,
                        token("string", pattern(compile("[\\s\\S]+")))
                    )
                )
            )
        )
        val insideInterpolation: Grammar
        run {
            val tokens: MutableList<Token> =
                ArrayList(js.tokens().size + 1)
            tokens.add(
                token(
                    "interpolation-punctuation",
                    pattern(compile("^\\$\\{|\\}$"), false, false, "punctuation")
                )
            )
            tokens.addAll(js.tokens())
            insideInterpolation = grammar("inside", tokens)
        }
        interpolation.patterns().add(
            pattern(
                compile("\\$\\{[^}]+\\}"),
                false,
                false,
                null,
                insideInterpolation
            )
        )
        val markup = prism4j.grammar("markup")
        if (markup != null) {
            GrammarUtils.insertBeforeToken(
                markup, "tag",
                token(
                    "script", pattern(
                        compile("(<script[\\s\\S]*?>)[\\s\\S]*?(?=<\\/script>)", CASE_INSENSITIVE),
                        true,
                        true,
                        "language-javascript",
                        js
                    )
                )
            )
        }
        return js
    }
}

@Extend("clike")
object Prism_kotlin {
    fun create(prism4j: Prism4j): Grammar {
        val kotlin = GrammarUtils.extend(
            GrammarUtils.require(prism4j, "clike"),
            "kotlin",
            { token -> "class-name" != token.name() },
            token(
                "keyword",
                pattern(
                    compile("(^|[^.])\\b(?:abstract|actual|annotation|as|break|by|catch|class|companion|const|constructor|continue|crossinline|data|do|dynamic|else|enum|expect|external|final|finally|for|fun|get|if|import|in|infix|init|inline|inner|interface|internal|is|lateinit|noinline|null|object|open|operator|out|override|package|private|protected|public|reified|return|sealed|set|super|suspend|tailrec|this|throw|to|try|typealias|val|var|vararg|when|where|while)\\b"),
                    true
                )
            ),
            token(
                "function",
                pattern(compile("\\w+(?=\\s*\\()")),
                pattern(compile("(\\.)\\w+(?=\\s*\\{)"), true)
            ),
            token(
                "number",
                pattern(compile("\\b(?:0[xX][\\da-fA-F]+(?:_[\\da-fA-F]+)*|0[bB][01]+(?:_[01]+)*|\\d+(?:_\\d+)*(?:\\.\\d+(?:_\\d+)*)?(?:[eE][+-]?\\d+(?:_\\d+)*)?[fFL]?)\\b"))
            ),
            token(
                "operator",
                pattern(compile("\\+[+=]?|-[-=>]?|==?=?|!(?:!|==?)?|[\\/*%<>]=?|[?:]:?|\\.\\.|&&|\\|\\||\\b(?:and|inv|or|shl|shr|ushr|xor)\\b"))
            )
        )
        GrammarUtils.insertBeforeToken(
            kotlin, "string",
            token(
                "raw-string",
                pattern(compile("(\"\"\"|''')[\\s\\S]*?\\1"), false, false, "string")
            )
        )
        GrammarUtils.insertBeforeToken(
            kotlin, "keyword",
            token(
                "annotation",
                pattern(
                    compile("\\B@(?:\\w+:)?(?:[A-Z]\\w*|\\[[^\\]]+\\])"),
                    false,
                    false,
                    "builtin"
                )
            )
        )
        GrammarUtils.insertBeforeToken(
            kotlin, "function",
            token("label", pattern(compile("\\w+@|@\\w+"), false, false, "symbol"))
        )

        // this grammar has 1 token: interpolation, which has 2 patterns
        val interpolationInside: Grammar
        run {


            // okay, I was cloning the tokens of kotlin grammar (so there is no recursive chain of calls),
            // but it looks like it wants to have recursive calls
            // I did this because interpolation test was failing due to the fact that `string`
            // `raw-string` tokens didn't have `inside`, so there were not tokenized
            // I still find that it has potential to fall with stackoverflow (in some cases)
            val tokens: MutableList<Token> =
                ArrayList(kotlin.tokens().size + 1)
            tokens.add(
                token(
                    "delimiter",
                    pattern(compile("^\\$\\{|\\}$"), false, false, "variable")
                )
            )
            tokens.addAll(kotlin.tokens())
            interpolationInside = grammar(
                "inside",
                token(
                    "interpolation",
                    pattern(
                        compile("\\$\\{[^}]+\\}"),
                        false,
                        false,
                        null,
                        grammar("inside", tokens)
                    ),
                    pattern(compile("\\$\\w+"), false, false, "variable")
                )
            )
        }
        val string = GrammarUtils.findToken(kotlin, "string")
        val rawString = GrammarUtils.findToken(kotlin, "raw-string")
        if (string != null
            && rawString != null
        ) {
            val stringPattern = string.patterns()[0]
            val rawStringPattern = rawString.patterns()[0]
            string.patterns().add(
                pattern(
                    stringPattern.regex(),
                    stringPattern.lookbehind(),
                    stringPattern.greedy(),
                    stringPattern.alias(),
                    interpolationInside
                )
            )
            rawString.patterns().add(
                pattern(
                    rawStringPattern.regex(),
                    rawStringPattern.lookbehind(),
                    rawStringPattern.greedy(),
                    rawStringPattern.alias(),
                    interpolationInside
                )
            )
            string.patterns().removeAt(0)
            rawString.patterns().removeAt(0)
        } else {
            throw RuntimeException(
                "Unexpected state, cannot find `string` and/or `raw-string` tokens " +
                        "inside kotlin grammar"
            )
        }
        return kotlin
    }
}

@Extend("markup")
object Prism_markdown {
    fun create(prism4j: Prism4j): Grammar {
        val markdown = GrammarUtils.extend(
            GrammarUtils.require(prism4j, "markup"),
            "markdown"
        )
        val bold = token(
            "bold", pattern(
                compile("(^|[^\\\\])(\\*\\*|__)(?:(?:\\r?\\n|\\r)(?!\\r?\\n|\\r)|.)+?\\2"),
                true,
                false,
                null,
                grammar("inside", token("punctuation", pattern(compile("^\\*\\*|^__|\\*\\*$|__$"))))
            )
        )
        val italic = token(
            "italic", pattern(
                compile("(^|[^\\\\])([*_])(?:(?:\\r?\\n|\\r)(?!\\r?\\n|\\r)|.)+?\\2"),
                true,
                false,
                null,
                grammar("inside", token("punctuation", pattern(compile("^[*_]|[*_]$"))))
            )
        )
        val url = token(
            "url", pattern(
                compile("!?\\[[^\\]]+\\](?:\\([^\\s)]+(?:[\\t ]+\"(?:\\\\.|[^\"\\\\])*\")?\\)| ?\\[[^\\]\\n]*\\])"),
                false,
                false,
                null,
                grammar(
                    "inside",
                    token("variable", pattern(compile("(!?\\[)[^\\]]+(?=\\]$)"), true)),
                    token("string", pattern(compile("\"(?:\\\\.|[^\"\\\\])*\"(?=\\)$)")))
                )
            )
        )
        GrammarUtils.insertBeforeToken(
            markdown, "prolog",
            token("blockquote", pattern(compile("^>(?:[\\t ]*>)*", MULTILINE))),
            token(
                "code",
                pattern(compile("^(?: {4}|\\t).+", MULTILINE), false, false, "keyword"),
                pattern(compile("``.+?``|`[^`\\n]+`"), false, false, "keyword")
            ),
            token(
                "title",
                pattern(
                    compile("\\w+.*(?:\\r?\\n|\\r)(?:==+|--+)"),
                    false,
                    false,
                    "important",
                    grammar("inside", token("punctuation", pattern(compile("==+$|--+$"))))
                ),
                pattern(
                    compile("(^\\s*)#+.+", MULTILINE),
                    true,
                    false,
                    "important",
                    grammar("inside", token("punctuation", pattern(compile("^#+|#+$"))))
                )
            ),
            token(
                "hr", pattern(
                    compile("(^\\s*)([*-])(?:[\\t ]*\\2){2,}(?=\\s*$)", MULTILINE),
                    true,
                    false,
                    "punctuation"
                )
            ),
            token(
                "list", pattern(
                    compile("(^\\s*)(?:[*+-]|\\d+\\.)(?=[\\t ].)", MULTILINE),
                    true,
                    false,
                    "punctuation"
                )
            ),
            token(
                "url-reference", pattern(
                    compile("!?\\[[^\\]]+\\]:[\\t ]+(?:\\S+|<(?:\\\\.|[^>\\\\])+>)(?:[\\t ]+(?:\"(?:\\\\.|[^\"\\\\])*\"|'(?:\\\\.|[^'\\\\])*'|\\((?:\\\\.|[^)\\\\])*\\)))?"),
                    false,
                    false,
                    "url",
                    grammar(
                        "inside",
                        token("variable", pattern(compile("^(!?\\[)[^\\]]+"), true)),
                        token(
                            "string",
                            pattern(compile("(?:\"(?:\\\\.|[^\"\\\\])*\"|'(?:\\\\.|[^'\\\\])*'|\\((?:\\\\.|[^)\\\\])*\\))$"))
                        ),
                        token("punctuation", pattern(compile("^[\\[\\]!:]|[<>]")))
                    )
                )
            ),
            bold,
            italic,
            url
        )
        add(GrammarUtils.findFirstInsideGrammar(bold), url, italic)
        add(GrammarUtils.findFirstInsideGrammar(italic), url, bold)
        return markdown
    }

    private fun add(grammar: Grammar?, first: Token, second: Token) {
        if (grammar != null) {
            grammar.tokens().add(first)
            grammar.tokens().add(second)
        }
    }
}

@Aliases("xml", "html", "mathml", "svg")
object Prism_markup {
    fun create(prism4j: Prism4j): Grammar {
        val entity =
            token("entity", pattern(compile("&#?[\\da-z]{1,8};", CASE_INSENSITIVE)))
        return grammar(
            "markup",
            token("comment", pattern(compile("<!--[\\s\\S]*?-->"))),
            token("prolog", pattern(compile("<\\?[\\s\\S]+?\\?>"))),
            token("doctype", pattern(compile("<!DOCTYPE[\\s\\S]+?>", CASE_INSENSITIVE))),
            token(
                "cdata",
                pattern(compile("<!\\[CDATA\\[[\\s\\S]*?]]>", CASE_INSENSITIVE))
            ),
            token(
                "tag",
                pattern(
                    compile(
                        "<\\/?(?!\\d)[^\\s>\\/=$<%]+(?:\\s+[^\\s>\\/=]+(?:=(?:(\"|')(?:\\\\[\\s\\S]|(?!\\1)[^\\\\])*\\1|[^\\s'\">=]+))?)*\\s*\\/?>",
                        CASE_INSENSITIVE
                    ),
                    false,
                    true,
                    null,
                    grammar(
                        "inside",
                        token(
                            "tag",
                            pattern(
                                compile("^<\\/?[^\\s>\\/]+", CASE_INSENSITIVE),
                                false,
                                false,
                                null,
                                grammar(
                                    "inside",
                                    token("punctuation", pattern(compile("^<\\/?"))),
                                    token("namespace", pattern(compile("^[^\\s>\\/:]+:")))
                                )
                            )
                        ),
                        token(
                            "attr-value",
                            pattern(
                                compile(
                                    "=(?:(\"|')(?:\\\\[\\s\\S]|(?!\\1)[^\\\\])*\\1|[^\\s'\">=]+)",
                                    CASE_INSENSITIVE
                                ),
                                false,
                                false,
                                null,
                                grammar(
                                    "inside",
                                    token(
                                        "punctuation",
                                        pattern(compile("^=")),
                                        pattern(compile("(^|[^\\\\])[\"']"), true)
                                    ),
                                    entity
                                )
                            )
                        ),
                        token("punctuation", pattern(compile("\\/?>"))),
                        token(
                            "attr-name",
                            pattern(
                                compile("[^\\s>\\/]+"),
                                false,
                                false,
                                null,
                                grammar(
                                    "inside",
                                    token("namespace", pattern(compile("^[^\\s>\\/:]+:")))
                                )
                            )
                        )
                    )
                )
            ),
            entity
        )
    }
}

object Prism_python {
    fun create(prism4j: Prism4j): Grammar {
        return grammar(
            "python",
            token(
                "comment", pattern(
                    compile("(^|[^\\\\])#.*"),
                    true
                )
            ),
            token(
                "triple-quoted-string", pattern(
                    compile("(\"\"\"|''')[\\s\\S]+?\\1"),
                    false,
                    true,
                    "string"
                )
            ),
            token(
                "string", pattern(
                    compile("(\"|')(?:\\\\.|(?!\\1)[^\\\\\\r\\n])*\\1"),
                    false,
                    true
                )
            ),
            token(
                "function", pattern(
                    compile("((?:^|\\s)def[ \\t]+)[a-zA-Z_]\\w*(?=\\s*\\()"),
                    true
                )
            ),
            token(
                "class-name", pattern(
                    compile("(\\bclass\\s+)\\w+", CASE_INSENSITIVE),
                    true
                )
            ),
            token(
                "keyword",
                pattern(compile("\\b(?:as|assert|async|await|break|class|continue|def|del|elif|else|except|exec|finally|for|from|global|if|import|in|is|lambda|nonlocal|pass|print|raise|return|try|while|with|yield)\\b"))
            ),
            token(
                "builtin",
                pattern(compile("\\b(?:__import__|abs|all|any|apply|ascii|basestring|bin|bool|buffer|bytearray|bytes|callable|chr|classmethod|cmp|coerce|compile|complex|delattr|dict|dir|divmod|enumerate|eval|execfile|file|filter|float|format|frozenset|getattr|globals|hasattr|hash|help|hex|id|input|int|intern|isinstance|issubclass|iter|len|list|locals|long|map|max|memoryview|min|next|object|oct|open|ord|pow|property|range|raw_input|reduce|reload|repr|reversed|round|set|setattr|slice|sorted|staticmethod|str|sum|super|tuple|type|unichr|unicode|vars|xrange|zip)\\b"))
            ),
            token("boolean", pattern(compile("\\b(?:True|False|None)\\b"))),
            token(
                "number", pattern(
                    compile(
                        "(?:\\b(?=\\d)|\\B(?=\\.))(?:0[bo])?(?:(?:\\d|0x[\\da-f])[\\da-f]*\\.?\\d*|\\.\\d+)(?:e[+-]?\\d+)?j?\\b",
                        CASE_INSENSITIVE
                    )
                )
            ),
            token(
                "operator",
                pattern(compile("[-+%=]=?|!=|\\*\\*?=?|\\/\\/?=?|<[<=>]?|>[=>]?|[&|^~]|\\b(?:or|and|not)\\b"))
            ),
            token("punctuation", pattern(compile("[{}\\[\\];(),.:]")))
        )
    }
}