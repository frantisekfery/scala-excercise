# Scala

Scala is a high-level, statically typed programming language that inter-corporates both OOP and FP paradigms, making it 
highly expressive and versatile. Scala stands for "Scalable Language", indicating that it is designed to grow with the 
needs of its users. It can be used to write small scripts, all the way up to building complex systems. Scala runs on the 
JVM, allowing it to leverage the vast Java ecosystem. It is compatible with Java libraries and frameworks, which means 
it can integrate seamlessly into a Java environment. Furthermore, Scala programs compile to Java bytecode, ensuring 
platform independence just like Java. Scala supports FP features such as pattern matching, immutability, and lazy 
computation. This allows developers to write more concise, high-level code, leading to increased programmer productivity
and code maintainability. Scala has powerful concurrency support, and libraries like AKKA make it easier to write 
concurrent and distributed systems. This makes Scala a popular choice for handling big data processing tasks, and 
notable use cases of Scala include Apache Spark and Kafka. Scala's static types help avoid bugs in complex applications, 
wherein the compiler can catch errors at compile-time. It also supports type inference, enhancing code readability and 
reducing verbosity. Scala's syntax deviates from Java. It enables more concise notation, reduces boilerplate code, and 
introduces several new features, such as operator overloading, optional parameters, named parameters, and raw strings.

## Scala takeaways

- high-level language
- statically-typed

Example:
```scala
var myVariable: Int = "Hello, world!"  // This will give an error at compile time
```
it is opposite to dynamically typed languages like JavaScript or Python
```python
var myVariable = 5;  // No error here...
myVariable = "Hello, world!"  // ...and no error here either.
```
- OOP and FP paradigms
- using JVM
- FP - pattern matching, immutability, lazy computation
- concurrency support
- type inference enhancing code readability and reducing verbosity
- reduce boilerplate code
- operator overloading, default parameters, named parameters and raw strings, string

Default parameters (You can provide default values for function parameters):
```scala
 def greet(name: String = "World"): Unit = println(s"Hello, $name!")
 greet() // prints: Hello, World!
 greet("Alice") // prints: Hello, Alice!
```
Named Parameters (This feature allows you to pass arguments to a function in a different order):
```scala
 def printCoordinates(x: Int, y: Int): Unit = println(s"Coordinates: x= $x, y= $y")
 printCoordinates(y = 10, x = 5) // prints: Coordinates: x= 5, y= 10
```
- values, variables, types: prefer vals over vars, all vals and vars have types, compiler automatically infers types 
when omitted
- expressions: in scala everything is an expression, void is returning Unit ()
- functions: def, instead of loop use recursion, especially tail recursion `@tailrec` where is use accumulator (fold) 
- call by name vs. call by value vs. call by need:
  - by value is standard (computed before call, the same value used everywhere):
```scala
def callByValue(x: Long): Unit = {
  println("By value: " + x)
  println("By value: " + x)
}
callByValue(System.nanoTime())
// Output:
// By value: 1234567890 (random example timestamp)
// By value: 1234567890 (the same timestamp, as the argument is computed once, before the function call)
```
  - by name is storing expression where is the variable use (lazy evaluation):
```scala
def callByName(x: => Long): Unit = {
  println("By name: " + x)
  println("By name: " + x)
}
callByName(System.nanoTime())
// Output:
// By name: 1234567891 (random example timestamp)
// By name: 1234567892 (different timestamp as 'System.nanoTime()' is evaluated every time it's used within the 
// function)
```
  - based on by name, there is a technic, which is called "call by need". It looks like this:
```scala
// Call By Need
def callByNeed(): Unit = {
  lazy val t = System.nanoTime()
  println("By need: " + t)
  println("By need: " + t)
}
callByNeed()
// Output:
// By need: 1234567893 (random example timestamp)
// By need: 1234567893 (the same timestamp, as the 'System.nanoTime()' stored in 't' is evaluated at most once, during 
// its first use)
```
- string operations: 
  - .toInt
```scala
val number: Int = "123".toInt // Now, number is integer 123
```
  - +: and :+
```scala
val newStr = "a" +: "-" +: "z" // Now, newStr is "a-z"
```
  - string interpolator - s type:
```scala
val name = "Alice"
println(s"Hello $name")  // It will print: Hello Alice
```
  - string interpolator - f type:
```scala
val topSpeed = 123.4567
val carName = "Ferrari"
println(f"$carName%10s top speed was $topSpeed%2.2f mph") // It will print:    Ferrari top speed was 123.46 mph
```
  - string interpolator - raw string:
```scala
val rawStr = """This is a raw
|String and it extends
|over multiple lines without needing escape sequences""".stripMargin
println(rawStr)
```
- OOP: class parameters class Person(name: String - is not field, it will be field when it is class Person(val name) 
```scala
class Person(name: String, age: Int) 
var peter = new Person("Peter", 20)
peter.name //name 
```
- Notations:
  - infix notation: If a method takes only one parameter, you can use the infix notation, which is more readable. It is 
used in testing frameworks. For example:
```scala
// Instead of this
person.likes("movie")
// You can write
person likes "movie"
```
  - prefix notation: It is used with operators +, -, ~, and !. For instance, -1 is equivalent to 1.unary_-. This works
  with methods that don't need any parameters.
```scala
// Instead of this
1.unary_-
// You can write
-1
```
  - postfix notation: It is used for methods which don't need any parameters. For example:
```scala
import scala.language.postfixOps
// Instead of this
person.isAlive
// You can write
person isAlive
```
  - apply notation: This is used when calling applies method of a class or object. It allows an object to be called like 
  a function. For example:
```scala
// Instead of this
person.apply("something")
// You can write
person("something")
```
- Scala Objects — objects are used to hold single instances of a class. An object is essentially a class that has 
exactly one instance (a singleton). Scala uses objects as a way to package methods that aren't associated with 
individual instances of a class.
```scala
object MyObject {
  def greet(): Unit = {
    println("Hello from MyObject!")
  }
}

// You can call the method as:
MyObject.greet()  // Prints: Hello from MyObject!
```
In contrast to objects, a class in Scala defines a type, along with methods, properties, and it can be instantiated.
```scala
class Person(val name: String) {
    def greet(): Unit = {
        println(s"Hello from $name!")
    } 
}

// You can instantiate and call class methods:
val person = new Person("John")
person.greet()  // Prints: Hello from John!
```
A class and object can share the same name. In this case, they are called companions. A companion class or companion 
object can access private members of its companion.
```scala
class Person(val name: String)

object Person {
    def apply(name: String): Person = new Person(name)
}

// You can use the apply method in the companion object to create an instance of Person
val person2 = Person("Doe")  // same as new Person("Doe")
```
- Inheritance
  Class Inheritance: In Scala, a class can inherit from another class. This is done using the extends keyword. When 
a class inherits from another class, it gets access to all non-private members (fields and methods) of the parent class.
```scala
class Parent {
  def greet(): Unit = println("Hello from Parent!")
}

class Child extends Parent {
  override def greet(): Unit = println("Hello from Child!")
}

val child = new Child
child.greet()  // Prints: Hello from Child!
```
Constructor Inheritance: If the parent class has a constructor, child classes extend it by passing arguments to the 
superclass’s constructor.
```scala
class Parent(name: String) {
  def greet(): Unit = println(s"Hello, I'm $name")
}

class Child(name: String, age: Int) extends Parent(name) {
  def displayAge(): Unit = println(s"I'm $age years old")
}

val child = new Child("John", 10)
child.greet()        // Prints: Hello, I'm John
child.displayAge()   // Prints: I'm 10 years old
```
Final keyword: If you don't want a class to be extended (i.e., you want to prevent inheritance), or to prevent method 
overriding, you can use the final keyword.
```scala
final class Parent {
  def greet(): Unit = println("Hello from Parent!")
}

// This will cause a compilation error:
// class Child extends Parent

class Parent2 {
  final def greet(): Unit = println("Hello from Parent!")
}

// This will also cause a compilation error:
// class Child2 extends Parent2 {
//  override def greet(): Unit = ...
// }
```
- **Polymorphism and type substitution:** This allows a subclass to be consumed as an instance of its superclass. This 
provides a way to use a class without knowing its precise type, which is at the very heart of object-oriented design.
- **Sealed classes**: Sealed classes are used for representing restricted class hierarchies, when a value can have one 
of the types from a limited set, but cannot have any other type. They are, in a sense, an extension of enum classes: 
the set of values for an enum type is also restricted, but each enum constant exists only as a single instance, whereas 
a subclass of a sealed class can have multiple instances which can contain state.
```scala
sealed class Parent

class Child1 extends Parent
class Child2 extends Parent
// No more subclasses are allowed elsewhere
```
- Abstract classes and Trait: both can have abstract and non-abstract members. The difference is that traits don't have 
constructor parameters, you can extend only one class, but you can implement more traits with keyword "with", 
traits = behaviour, abstract class = thing
- Scala type hierarchy — ANY (AnyRef for objects or Null and AnyVal for primitives) and for everything when it is empty,  
use Nothing.
- Access modifiers: private, protected, none (public)
- Generics: the same as in java covariance (in collections where you can instead of list of Dog treated as list of 
Animals where you can store also Cats), invariance (no substitution, just exact type) and contravariance (it is useful 
when you have a function that works for a generic type, and you want to use it for a more specific type. For example, if
a function can handle any Animal, you can use that function to handle a Dog, but not vice versa)
- Anonymous Classes — the same as in java
- Case Classes:
  - class parameters are fields without put val into constructor
  - sensible toString println(instance- = println(instance.toString-
  - equals and hashCode implemented OOTB
  - have a handy copy method (use copy with named parameters what you want to change-
  - have companion objects, so we can instanciate it by Person("Frantisek")
  - are serializable by default — used in distributed systems as Akka
  - have extractor patterns, so can be used in pattern matching
  - we can use case keyword with an object
- Exceptions: the same as in java, the output from throw new Exception is Nothing
- Packaging and Imports: the same as java, specific is that you can use alias for import if you have the same name and 
also package object that holds standalone methods/constants - there can be just one for package
- Functions:
  - all scala functions are objects with overriding apply method depending on how many parameters we want to use.
  - There are Function1-22 traits.
  - There is a syntax sugar for writing function as anonymous function (lambda) — val doubler = (x: Int) => x * 2, you
  have to call doubler() with parentesis, because otherwise it will give you an instance of val doubler.
  - Higher order function (HOF- are these function which have function as a parameter.
  - Curried functions are functions with these syntax: Int => Int => Int so you can use it like this: 
  curriedFunction()()
- For comprehensions:
  - Chainable operations: For-comprehensions allow chaining of multiple operations, making complex data manipulation 
  sequences more understandable.
  - Conditional Logic: They can incorporate conditional logic (i.e., using 'if' statements within the loop), letting you 
  filter elements directly within the comprehension.
  - Nested Loops: For-comprehensions can contain nested loops, which are useful for working with nested data structures.
  - Yield Value: In some languages like Scala, the 'yield' keyword is used in for-comprehensions to produce values that 
  are collected into a new collection.
  - Improves Readability: For-comprehension often improves code readability and maintainability. This is particularly 
  important in large codebases or when writing functional style code.
  - Monad Operations: In some functional programming languages, for-comprehensions represent a sequence of monadic 
  operations, and thus are a powerful tool expressing a series of dependent computations in a clean and readable way.
- Collections:
  - Immutable collections—Set (HashSet, SortedSet-, Seq (Indexed - Vector, String, Range and Linear - List, Stream, 
  Stack, Queue), Map (HashMap, SortedMap)
  - Mutable collections - Set (HashSet, LinkedHashSet-, Seq (Indexed - StringBuilder, ArrayBuffer - Buffer - ListBuffer 
  and Linear - linkedList, MutableList), Map (HashMap, MultiMap)
  - List vs Vector = List keeps reference to tail, updating an element in the middle takes long, Vector depth of the 
  tree is small, needs to replace an entire 32-element chunk.
  - Tuples and Maps: Tuples = (42, "RockTheJVM") or 42 -> "RockTheJVM", Map is creating as tuple of key and value
- Options: great for for-comprehensions Option can be Some(value) or None
- Either: right, left part It's used when you want to compute a value that may either result in an error or a successful
value, and you need more information about the error. Either has two instances, Left and Right. By convention, Left is
used for failure and Right is used for success.
- Failure handling: Try can be Success(value) Failure(exception)
- Pattern Matching: you can PM basically everything:
  - constant
  - _ = match everything - it is a wildcard
  - variable, and if you give a name, then you can use it in some expression
  - tuples or nested tuples
  - case classes - constructor pattern, also can be nested
  - list patterns (case List(1, _, _, _) = extractor, case List(1,_,*) = whatever length, case 1 :: List(_) or case 
  List(1,2) :+ 42 = infix pattern)
  - Name binding: case nonEmptyList @ Cons(_, _) if case is matched the line is storing value Cons(_, _) into val 
  nonEmptyList
  - multi pattern: case Empty | Cons(0, _) => // compound pattern
  - if guards: case Cons(_, Cons(specialElement, _)) if specialElement % 2 == 0 => // guard
- Implicits