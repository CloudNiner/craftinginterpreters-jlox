# Ch 3: The Lox Language

## 3.1 Hello, Lox

Here’s Lox!

```
// Your first lox program
print "Hello, World!"
```

The Lox interpreter written by the author is open source, so let’s clone the repo and try to run this code!

[https://github.com/munificent/craftinginterpreters](https://github.com/munificent/craftinginterpreters)

Install Dart `~2` via Brew following instructions on Dart install page. I initially installed latest Dart 3 which didn’t work because:

> The lower bound of "sdk: '\>2.11.0 \<3.0.0'" must be 2.12.0' or higher to enable null safety.

- [Dart Docs: Null Safety](https://dart.dev/null-safety)
- [GitHub comment with brew instructions for Dart 2.12](https://github.com/munificent/craftinginterpreters/issues/1122#issuecomment-1555117860)

Then `make jlox` and:

```
./jlox
> print "Hello World!";
Hello World!
```

🎉 🎉 🎉

## 3.X The Rest of Lox

### Data Types and Operators

The remaining sections of this chapter review in detail the syntax and capabilities of Lox. See below for an abbreviated version.
Lox is a high level language with automatic memory management via garbage collection. For more on garbage collection see [A Unified Theory of Garbage Collection](https://researcher.watson.ibm.com/researcher/files/us-bacon/Bacon04Unified.pdf).

```c
// boolean
true;
false;

// strings
"foo";

// numbers (all types of numbers double precision float)
1;
1.23;

// null
nil;

// arithmetic expressions only work on numbers
// except + concatenates two string types
1 + 2;  // infix operators +-*/
-1; // prefix operator, to negate

// comparison operators < > <= >= 
1 < 2; // true

// equality operators, different types are never equal
1 == 1; // true
2 != 1; // true
1 == "1"; // false

// logical operator ! and or
// no || && !
!true; // false
true and true; // true
true or false; // true
```

To simplify things, all of the above operations have the same precedence and associativity as they do in C. `(` and `)` can be used to group operations.

Also to simplify, this is it. No modulo, bitwise operators, etc! Bonus points for implementing!

### Statements

> expressions produce values, statements produce effects

Examples: `print {string};` - print the value to the console.

`()` group expressions, `{}` group statements via a **block**.

### Variables

Variables save the result of an expression for use in future evaluation.

In Lox, defined with `var` and if not set, the value defaults to `nil`. Mostly variable scoping rules work like in C.

### Control Flow

```c
// only three!
// if
if (condition) { } else { }

// while
while (condition) { }

// for
for (var i = initial_value; condition; increment) { }
```

> No for-in loop is a concession I made because of how the implementation is split across chapters. A for-in loop needs some sort of dynamic dispatch in the iterator protocol to handle different kinds of sequences, but we don’t get that until after we’re done with control flow. We could circle back and add for-in loops later, but I didn’t think doing so would teach you anything super interesting.

### Functions

```c
# function call
var result = doTheThing();

# function definition
fun doTheThing() {
 return 1 + 1;
}
```

If you don’t use the `()` at a call site you get the function reference.

No return statement implicitly returns `nil`.

Some semantics (these two aren’t the same!):

- **argument**: The value(s) you call a function with. A function call has an “argument list”.
- **parameter**: variable that holds the argument inside the function block. This means a function declaration has a “parameter list”.

#### Closures

Lox makes functions first class citizens! They’re real values.

> As you can imagine, implementing closures adds some complexity because we can no longer assume variable scope works strictly like a stack where local variables evaporate the moment the function returns. We’re going to have a fun time learning how to make these work correctly and efficiently.

With closures, block scope, and dynamic typing — Lox can be sorta functional.

### Classes

Lox can also be object oriented with some basic Class capabilities.

Apparently a fair number of books that implement a language leave classes out? So here they are for completeness sake.

#### Classes vs Prototypes

Prototypal languages and implementations of them are simpler — the paradigm pushes implementation complexity to the user. Lots of times this leads to users reimplementing classes.

#### Classes in Lox

Pretty simple:

```c
class Breakfast {
 init(meat) {
    this.meat = meat;
    }

    cook() {
    }

    serve(who) {
  print "Enjoy your " + this.meat + "!";
    }
}
```

Classes are first class in Lox, they can be assigned to variables:

```c
var breakfastVar = Breakfast
```

And instantiated:

```c
var myBreakfast = Breakfast()
```

Attach state vars directly to instances:

```c
myBreakfast.meat = "sausage";
// auto create if they don't exist
myBreakfast.eggs = "scrambled";
```

Inheritance — subclass `Breakfast` with `Brunch`:

```c
class Brunch < Breakfast {
 init(meat, drink) {
  super.init(meat);
  this.drink = drink;
    }
}
```

Single inheritance, methods on super class are available to subclasses via `super`

> Lox is not a pure OOP language because not all objects are instances of a class. This is because we don’t implement classes until well after we start working with the built-in types, which would have presented an ordering and complexity problem. Oh well!

## The Lox Standard Library

Welp. It’s pretty minimal. We need a way to perform benchmarking, so we have a `clock()` function, returning the number of seconds since the program started, to add to our `print` statement.

All that stuff like file I/O, string functions, math functions, anything you can think of from the Python std library

## Challenges

1. Write some sample Lox programs and run them (you can use the implementations of Lox in my repository). Try to come up with edge case behavior I didn’t specify here. Does it do what you expect? Why or why not?

> Was able to do this via `jlox` while working through this section to test some aside questions, like does `super` exist before I saw it in the classes section.

2. This informal introduction leaves a lot unspecified. List several open questions you have about the language’s syntax and semantics. What do you think the answers should be?
 - Variable hoisting?
 - Ternary operators?
 - …

3. Lox is a pretty tiny language. What features do you think it is missing that would make it annoying to use for real programs? (Aside from the standard library, of course.)

 - A usable REPL! No history recall. Can’t enter multiline statements line by line.
 - Optional typing
 - More functional stuff?
 - Protocols
 - Generics
 - Callbacks/async?
 - Package manager / imports

## Design Note: Expressions and Statements

Lox has expressions and statements. The latter isn’t strictly necessary for a usable language. Think Haskell, Scala, Ruby.

Remember, expressions produce a value and statements produce an effect with no value.

So if you choose to not have statements, statement-like expressions must return some kind of value. What should a `for` loop return? What are the performance implications of storing evaluations that may rarely see use?

Lox chose to add statements to make the language feel more like C and to avoid having to make some of the above design decisions in a language with C-like syntax, which can get hairy? Why?
