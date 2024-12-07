# Chapter 1

## 1.0 Foreward

[https://craftinginterpreters.com/contents.html](https://craftinginterpreters.com/contents.html)

Book available at bookshop.org.

### Why?

I’ve always been amazed by compilers and programming languages? How do they work? How does text become computer programs?

This book seems like a very friendly and theory-light introduction to the topic written by someone that avoids falling into overly academic tropes and writing styles. Let’s see how it goes!

## 1.1 Introduction

[https://craftinginterpreters.com/welcome.html](https://craftinginterpreters.com/welcome.html)

This book will work through two complete interpreters for a full fledged language. We’ll learn how to make a programming language!

Chapter by chapter we’ll have a working implementation. The code samples may be light on details like `private` in the right place. That’s left to you.

The challenges in this book aren’t necessary to complete to get a working interpreter. They’re extras for more deep diving on related topics to expand your knowledge!

### The First Interpreter

We’ll call it `jlox` implemented in Java. The focus is on `concepts` and it’ll probably be slow.

### The Second Interpreter

Now its time for `clox` implemented in C. You’ll need to know the language and be comfortable with pointers and manual memory management. This will make our _correct_ implementation **fast**!

Maybe from here I can do a `rulox` in Rust!

[https://craftinginterpreters.com/introduction.html](https://craftinginterpreters.com/introduction.html)

## 1.1a: Domain Specific Languages

> There are at least six domain-specific languages used in the [little system I cobbled together](https://github.com/munificent/craftinginterpreters) to write and publish this book. What are they?

[https://en.wikipedia.org/wiki/Domain-specific\_language](https://en.wikipedia.org/wiki/Domain-specific_language):

> A domain-specific language (DSL) is a computer language specialized to a particular application domain. This is in contrast to a general-purpose language (GPL), which is broadly applicable across domains.

Not particularly helpful…kind of a self defining definition.

Here’s some guesses?

- Markdown
- Make
- YAML
- .gitignore?
- HTML
- CSS
- SASS
- Xcode `.xcodeproj` XML?
- IntelliJ `.iml` XML?

## 1.1b: Java “Hello World”

> Get a “Hello, world!” program written and running in Java. Set up whatever makefiles or IDE projects you need to get it working. If you have a debugger, get comfortable with it and step through your program as it runs.

### Installing Java

Kept it simple, just did a `brew install java` and added the brew jdk path to zshrc. There appear to be tools like jabba (last updated 2019), jenv (doesn’t actually install java), manual downloads. All seem more complicated and I just want to get going. The current java version will be fine.

### Editor

Was gonna do vscode extensions but I kinda wanna try IntelliJ. It was really nice when I last used it and I figure it’s a great way to get into Java.

Yeah. This was the right choice. I was able to easily create a project, build and run it. I could select a project specific JDK to use as well, so I did that. Forget brew!

Also installed a keybinding suggester plugin and `IdeaVim`. We’re in business.

## 1.1c: C “Hello World”

> Get a “Hello, world!” program written and running in C. Set up whatever makefiles or IDE projects you need to get it working. If you have a debugger, get comfortable with it and step through your program as it runs.

TODO: Saved for before starting the C interpreter

> To get some practice with pointers, define a [doubly linked list](https://en.wikipedia.org/wiki/Doubly_linked_list) of heap-allocated strings. Write functions to insert, find, and delete items from it. Test them.

TODO: Saved for before starting the C interpreter

## What’s in a name?

Coming up with a name is hard!

Here’s some considerations to keep in mind when doing so…

- It isn’t in use: legal!
- It’s easy to pronounce: It’s gonna be said a lot so make it easy
- It’s distinct to search for: Don’t make people sob when they search and get something else
- It doesn’t have negative connotations
 	- Example: “nimrod” project
