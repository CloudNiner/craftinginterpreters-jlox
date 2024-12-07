# Section II - A Tree-Walk Interpreter

Time for `jlox` to come into existence!

Gonna implement it all in Java. A few thousand lines and we’ll have a functional interpreter. Amazing.

The first step is to implement scanning, where we take the raw source code characters and group them into lexical tokens. As noted in Ch2, tokens are the meaningful “words” and “punctuation” that make up the language grammar.

## Ch 4: Scanning

### 4.1.1: Error Handling

Error handling is kept very simple, but remains in the spirit of decoupling whatever is generating the errors from whatever is reporting and rendering them to the user. TODO: Refactor to some more generic error interface.

### 4.2: Lexemes and Tokens

This is where the real implementation begins!

I always thought that there was a smarter way than having an Enum of all the possible lexemes. But I guess not!

In this section we build our enum of Token types and stub the Token class.

### 4.3: Regular Languages and Expressions

How a Scanner works:

Start at the first character. Go forward until you figure out what lexeme that token is a part of. Then emit that lexeme as a token. Then continue at the first character after the emitted lexeme.

For languages that have a simple enough grammar to represent as regular expressions, the language is considered a _regular language_.

Tools like [Lex](https://en.wikipedia.org/wiki/Lex_(software)) and [Flex](https://github.com/westes/flex) are designed to generate a scanner by providing your grammar as regexes.

We’re glossing over a ton of theory here, for more try Compilers: Principles, Techniques, and Tools.

### 4.4: The Scanner Class

Literally typing — just mock up the non functional shell of `Scanner`.

### 4.5: Recognizing Lexemes

Fun to work through this section, the implementation is different from how I would have done it (avoid mutating key state in an instance method like `advance()` but it works and is pretty clean. Just hard to track the logic flow. TODO: Is there a nice refactor here?

Straightforward to add junit5 tests after I figured out how to do that in IntelliJ:

- [https://junit.org/junit5/docs/current/user-guide/#writing-tests](https://junit.org/junit5/docs/current/user-guide/#writing-tests)
- [https://www.jetbrains.com/help/idea/testing.html#add-testing-libraries](https://www.jetbrains.com/help/idea/testing.html#add-testing-libraries)

### 4.6: Longer Lexemes

Add support for comments, string literals, and number literals.

TODO: Switch to only single line strings since it’s a little harder

TODO: Implement `equals()` and `hashCode()` for `Token` to make our tests more correct

### 4.7 Reserved Words and Identifiers

**Introducing!** the _maximal munch_ rule which states that the scanner will always match the lexeme that matches the most characters.

Example: `---a;`. Is this `- --a ;` which is valid C code, or `-- -a ;` which isn’t? If the former, we need to keep state of surrounding tokens which is very complicated. So actually we always scan it as the latter, which gives us a syntax error in the parser later.

Related — recall that earlier our scanner correctly scans  `<=` as `LESS_EQUAL` rather than `LESS` + `EQUAL`, according to the maximal munch rule.

## Ch 4: Challenges

1. The lexical grammars of Python and Haskell are not regular. What does that mean, and why aren’t they?

It means the grammars can be fully represented by regular expressions.

Is the hint here Haskell, and that because its a functional language and has things like lambdas, anonymous functions, etc, that that introduces complexity that can’t be represented by regexes?

2. Aside from separating tokens—distinguishing print foo from printfoo—spaces aren’t used for much in most languages. However, in a couple of dark corners, a space does affect how code is parsed in CoffeeScript, Ruby, and the C preprocessor. Where and what effect does it have in each of those languages?

I don’t know enough about these languages to know this offhand, perhaps an interesting question to look at later.

3. Our scanner here, like most, discards comments and whitespace since those aren’t needed by the parser. Why might you want to write a scanner that does not discard those? What would it be useful for?

Static analysis tools that use comments to indicate things about nearby source code, like Python’s `type: ignore`.

4. TODO: Add support to Lox’s scanner for C-style /*...*/ block comments. Make sure to handle newlines in them. Consider allowing them to nest. Is adding support for nesting more work than you expected? Why?

Hard? Use a counter, increment at each `/*`  and decrement the counter every time you find `*/`. While counter `> 0` you’re inside a block comment.

Commit: [https://github.com/CloudNiner/craftinginterpreters-jlox/commit/7ef21420353784f7bcd775442a987bd396d6ef1a](https://github.com/CloudNiner/craftinginterpreters-jlox/commit/7ef21420353784f7bcd775442a987bd396d6ef1a)

## Ch 4: Implicit Semicolons

Key takeaway — Javascript gets more and more insane the more you read about it:

> Javascript treats all your newlines as meaningless whitespace _unless_ it encounters a parse error. Then it backtracks and tries to turn the previous newline into a semicolon to get something grammatically valid.

Key takeaway: **Don’t do what Javascript did LOL**

Also, in Python-land — newlines being significant whitespace as statement delimiters means that a statement cannot appear inside an expression. This is why Python lambdas cannot span two lines!

## Ch 5: Representing Code

In this chapter, we write the parser that turns the string of tokens from the previous chapter into an abstract syntax tree!

### The Arithmetic Example

Look at:
`1 + 2 * 3 - 4`

Pretend your brain is an interpreter. How do you evaluate this expression?

You know order of operations, so you probably do something like:

```c
1 + 6 - 4
7 - 4
3
```

Visually:
![Visual example of expression as a tree](images/Screenshot%202024-07-11%20at%2013.30.27.png)

**Key takeaways:**

- Given an arithmetic expression, a human could likely generate this visual tree pretty easily. And with a tree, its trivial to evaluate by combining the leaf nodes all the way up to the root.
- So…for `lox` if we can define the grammar (the equivalent of “order of operations”) we could construct the same type of tree and thus evaluate our code

### 5.2.2 Disoriented Objects

This section just takes a moment to suggest that it would be smart to separate implementation from the data structure classes. Our `Expr` classes will be dumb data containers and we’ll put the implementation elsewhere because these “tree data” classes are used by both the parser and interpreter. Its gonna get messy if we start putting implementation directly on these classes.

## Ch 5.1: Context-Free Grammars

In the last chapter, our lexical token grammar was a _regular language_.

But here, that’s not powerful enough **because we need to be able to handle arbitrarily deeply nested expressions**. Imagine trying to do that with regexes! Yikes.

Introducing [context-free grammars](https://en.wikipedia.org/wiki/Context-free_grammar "Context-Free Grammar (Wikipedia)") which is one of many types of [formal grammars](https://en.wikipedia.org/wiki/Formal_grammar "Formal Grammar (Wikipedia)").

A formal grammar specifies which strings are valid and which are not using specific rules.

| Terminology              | Lexical Grammar (Ch 4) | Syntactic Grammar (now) |
| ------------------------ | ---------------------- | ----------------------- |
| The “alphabet” is…       | Characters             | Tokens                  |
| A “string” is…           | Lexeme or Token        | Expression              |
| It’s implemented by the… | Scanner                | Parser                  |

Combining these two steps turns individual source characters into executable expressions!

### 5.1.1 Rules for Grammars

Think of the rules as a game you “play” by:

Start with the rules and use them to generate strings in the grammar. Then in each step of the game pick a rule and follow what it tells you to do.

Each rule (Production) has a head and a body. For example:

> `protein -> crispiness "bacon"`

`protein` is the **head**, `crispiness "bacon"` is the **body**.

In the above Production we also have both **terminal** and **nonterminal** statements. A terminal is like a literal — `"bacon"` in the above example. Nonterminals are like variables, they’re a reference to another rule in the grammar — `crispiness` in the above example.

The rest of this section of the book walks through a more complete set of grammar rules for generating breakfast menu items and walks through generating a string entirely of terminals by substituting rules.

### Ch 5.1.2: The Lox grammar notation

The notation defined in this section will be used to precisely describe the Lox grammar for the remainder of the book. Consider it a handy reference!

A Production (rule): `head -> body`

**In the body, terminals are surrounded by `"`. Nonterminals are not quoted.**

**`|` allows a series of productions** which condenses:

```c
bread -> "toast"
bread -> "biscuits"
bread -> "English muffin"
```

to:

```c
bread -> "toast" | "biscuits" | "English muffin"
```

**`()` allows grouping. `|` is allowed within a grouping.** This allows condensing  rules that only have terminal bodies into the definition of other more complicated productions. For example:

```c
cooked -> "scrambled"
cooked -> "poached"
cooked -> "fried"

protein -> cooked "eggs"
```

becomes:

```c
protein -> ( "scrambled" | "poached" | "fried" ) "eggs"
```

Since `cooked` isn’t used anywhere else in our grammar we don’t have to define it separately (but we could!)

**A postfix `*` repeats the previous symbol zero or more times.**
**A postfix `+` repeats the previous symbol one more more times.**
These are our recursion operators! They allow us to avoid defining a separate rule any time we want to recurse:

```c
crispiness -> "really"
crispiness -> "really" crispiness
```

becomes:

```c
crispiness -> "really" "really"*
// OR
crispiness -> "really"+
```

**A postfix `?` is for an optional production. The thing before it can appear zero or one times.** For example:

```c
breakfast -> protein "with" breakfast "on the side"
breakfast -> protein
```

becomes:

```c
breakfast -> protein ("with" breakfast "on the side")?
```

#### A Complete Example

Given the rules above, the more verbose breakfast menu grammar from 5.1.1:

```c
breakfast  → protein "with" breakfast "on the side" ;
breakfast  → protein ;
breakfast  → bread ;

protein    → crispiness "crispy" "bacon" ;
protein    → "sausage" ;
protein    → cooked "eggs" ;

crispiness → "really" ;
crispiness → "really" crispiness ;

cooked     → "scrambled" ;
cooked     → "poached" ;
cooked     → "fried" ;

bread      → "toast" ;
bread      → "biscuits" ;
bread      → "English muffin" ;
```

“Simplifies” to:

```c
breakfast -> protein ("with" breakfast "on the side")? | bread ;
protein ->  "really"+ "crispy" "bacon" | 
   "sausage" | 
   ("scrambled" | "poached" | "fried") "eggs" ;
bread ->  "toast" | "biscuits" | "English muffin" ;
```

### Ch 5.1.3 A Grammar for (Basic) Lox Expressions

- **Literals** Numbers, strings, bool, nil
- **Unary Expressions** A prefix `!` for logical not and `-` to negate a number
- **Binary Expressions** The infix arithmetic operations `+-*/` and logical operators `==, !=, >=, >, <=, <`
- **Parenthesis** A pair of `(` and `)` to wrap an expression
- **CAPITALIZE metasyntax** terminal whose text representation may vary, like `STRING` and `NUMBER` which represent any string and number literal, respectively

This leads to the basic grammar:

```c
expression -> literal | unary | binary | grouping
literal    ->  NUMBER | STRING | "true" | "false" | "nil"
grouping   ->  "(" expression ")"
unary      -> ( "!" | "-" ) expression
binary     -> expression operator expression
operator   -> "==" | "!=" | "<" | "<=" | ">" | ">=" | "+"  | "-"  | "*" | "/"
```
