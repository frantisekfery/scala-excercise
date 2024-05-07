# Scala

Scala is high-level, statically typed programming language that intercorporates both OOP and FP paradigms, making it 
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

- high level language
- statically-typed

Example:
```scala
var myVariable: Int = "Hello, world!"  // This will give an error at compile time
```
it is opposite to dynamically-typed languages like JavaScript or Python
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
- expressions: in scala everything is an expression, void is returning Unit
- functions: def, instead of loop use recursion, especially tail recursion `@tailrec` where is use accumulator (fold) 
- call by name vs call by value vs call by need:
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
  - based on by name, there is a technic, which is called call by need, it looks like this):
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
- OOP: class parameters class Person(name: String- is not field, it will be field when will be class Person (val name: 
```scala
class Person(name: String, age: Int) 
var peter = new Person("Peter", 20)
peter.name //name 
```
String-, overloading constructor you can use only another constructor, all operators (+, -- are methods, in Akka actors (?, !-, notations:
  - infix notation (operator notation- - if method has only one parameters (person.likes("movie"- = person likes "movie"-
  - prefix notation (-1 is equivalent to 1.unary_- it can be use only for - + ~ ! operators-
  - postfix notation - if the metnod has no parameters (person.isAlive = person isAlive-
  - apply notation - if the method is called apply (person.apply(- = person(- or person.apply("something"- = person("something"--
- Scala Objects - Scala doesn't have class-level functionality like java with keyword "static", it has better way = create both object (singleton instance- (as in java static or level functionality- and class - instance-level functionality, it is called comopanions, if you have in object apply method then you can instantiate it like that Person.apply(mother, father, "childName"- = Person(mother, fatker, "childName"- - factory method.
- Inheritance: class inheritance, in child you can override methods, when parent has constructor, also it have to be extended with proper arguments for parent constructor, you can forbid inheritance or overrideing methods by keyword "final". Type substitution (polymorphism- - it means you can instantiate child into parent, super is used the same way as in java, there is also option to seal the class by keyword "sealed" you can inherite from parent only in file where is parent.
- Abstract classes and Trait: both can have abstract and non-abstract members. The difference is that traits don't have constructor parameters, you can extends only one class, but you can implement more traits with keyword "with", traits = behaviour, abstract class = thing
- Scala type hierarchy - ANY (AnyRef for objects or Null and AnyVal for primitives- and for everything when it is empty use Nothing.
- Access modifiers: private, protected, none (public-
- Generics: the same as in java covariance (in collections where you can instead of list of Dog treated as list of Animals where you can store also Cats-, invariance (no substitution, just exact type- and contravariance - it is useful when you have a function that works for a generic type, and you want to use it for a more specific type. For example if a function can handle any Animal, you can use that function to handle a Dog, but not vice versa.
- Anonymous Classes - the same as in java
- Case Classes:
  - class parameters are fields without put val into constructor
  - sensible toString println(instance- = println(instance.toString-
  - equals and hashCode implemented OOTB
  - have handy copy method (use copy with named parameters what you want to change-
  - have companion objects so we can instanciate by Person("Frantisek"-
  - are serializable by default - used in distrubuted systems as Akka
  - have extractor patterns, so can be used in pattern matching
  - we can use case keyword with object
- Exceptions: the same like in java, the output from throw new Exception is Nothing
- Packaging and Imports: the same as java, specific is that you can use alias for import if you have the same name and also package object that hold standalone methods/constants - there can be just one for package
- Functions:
  - all scala functions are objects with overriding apply method depending on how many parameters we want to use.
  - There are Function1-22 traits.
  - There is a syntax sugar for writing function as anonymous function (lambda- - val doubler = (x: Int- => x * 2, you have to call doubler(- with parentesis, because otherwise it will give you an instance of val doubler.
  - Higher order function (HOF- are these function which have function as a parameter.
  - Curried functions are functions with these syntax: Int => Int => Int so you can use it like this: curriedFunction(-(-
- For comprehensions:
  - Chainable operations: For-comprehensions allow chaining of multiple operations, making complex data manipulation sequences more understandable.
  - Conditional Logic: They can incorporate conditional logic (i.e., using 'if' statements within the loop-, letting you filter elements directly within the comprehension.
  - Nested Loops: For-comprehensions can contain nested loops, which are useful for working with nested data structures.
  - Yield Value: In some languages like Scala, the 'yield' keyword is used in for-comprehensions to produce values that are collected into a new collection.
  - Improves Readability: For-comprehensions often improve code readability and maintainability. This is particularly important in large codebases or when writing functional style code.
  - Monad Operations: In some functional programming languages, for-comprehensions represent a sequence of monadic operations, and thus are a powerful tool expressing a series of dependent computations in a clean and readable way.
- Collections:
  - Immutable collections - Set (HashSet, SortedSet-, Seq (Indexed - Vector, String, Range and Linear - List, Stream, Stack, Queue-, Map (HashMap, SortedMap-
  - Mutable collections - Set (HashSet, LinkedHashSet-, Seq (Indexed - StringBuilder, ArrayBuffer - Buffer - ListBuffer and Linear - linkedList, MutableList-, Map (HashMap, MultiMap-
  - List vs Vector = List keeps reference to tail, updating an element in the middle takes long, Vector depth of the tree is small, needs to replace an entire 32-element chunk.
  - Tuples and Maps: Tuples = (42, "RockTheJVM"- or 42 -> "RockTheJVM", Map is creating as tuple of key and value
- Options: very good for for-comprehensions Option can be Some(value- or None
- Failure handling: Try can be Success(value- Failure(exception-
- Pattern Matching: you can PM basically everything:
  - constant
  - _ = match everything - it is a wildcard
  - variable, and if you give a name, then you can use it in some expression
  - tuples or nested tuples
  - case classes - constructor pattern, also can be nested
  - list patterns (case List(1, _, _, _- = extractor, case List(1,_*- = whatever length, case 1 :: List(_- or case List(1,2,- :+ 42 = infix pattern-
  - Name binding: case nonEmptyList @ Cons(_, _- if case is matched the line is storing value Cons(_, _- into val nonEmptyList
  - multi pattern: case Empty | Cons(0, _- => // compound pattern
  - if guards: case Cons(_, Cons(specialElement, _-- if specialElement % 2 == 0 => // guard