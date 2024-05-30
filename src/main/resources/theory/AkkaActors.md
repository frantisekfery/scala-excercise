# Akka Actors

Akka Actors is a model for building highly concurrent, distributed, and fault-tolerant systems. 

## Key aspects:
1. **Actor Model**: Akka uses the actor model, which is a mathematical model of concurrent computation that treats 
"actors" as the universal primitives of concurrent computation.
2. **Easy Concurrency and Distribution**: Akka makes it easier to write safe concurrent and parallel systems. Each Actor
can execute independently of others, and communication happens via message passing, which allows you to write code 
without worrying about mutual exclusion and synchronization. So the threads running the actors are non-blocking for IO 
and actor message interactions.
3. **Fault-tolerance**: Actor systems are designed around a "let it crash" philosophy – if an actor fails, it is simply
stopped and another takes its place. This forms a strong basis for building systems that self-heal, where a failing 
component does not lead to system-wide failure.
   To prevent message loss, a common practice is to use 'persistent actors'. When a persistent actor receives a command, 
it persists the generated event before processing the command and changing its state. This way, when the actor is 
restarted, it can replay the events to restore its state, ensuring no message is lost.
4. **Location Transparency**: Akka Actors provide location transparency. Actors can seamlessly be distributed across 
different nodes in the network, and the system manages routing and location of actors without the programmer needing to 
worry about it.
5. **Lightweight**: Actors are very lightweight concurrency entities. They are cheap to instantiate and consume less 
memory, so millions of Actors can exist in the same system without impacting performance.
6. **Asynchronous Communication and Mailboxes**: Actors in Akka communicate with each other via asynchronous message 
passing. Specifically, each Actor has a mailbox where incoming messages get stored. Instead of directly invoking methods
on an instantiated object as one would do in traditional object-oriented programming, you send a message to an actor to 
request an action to be performed or a computation to be processed. These messages, stored in the actor's mailbox, get 
processed in a First-In-First-Out (FIFO) manner. One crucial point to bear in mind is that each actor processes these 
messages sequentially, one at a time, which effectively removes the need for explicit locks and condition variables. 
This method guarantees that no two messages will be processed simultaneously, thus avoiding state corruption through 
concurrent modifications. This approach leads to systems that are not only more efficient but also simpler to understand 
and reason about, allowing safe concurrent processing and consistent state management. It essentially forms the heart of 
the Actor Model paradigm.
7. **Actor Hierarchies**: Akka actors are arranged in hierarchies. Each actor has a parent and potentially many 
children. Parents supervise their children and handle their failures, leading to a natural fault tolerance model.
8. **Strongly Encapsulated**: Each Actor has a mailbox and state that are not directly accessible from outside, 
promoting a high level of encapsulation, similar to OOP but with much stronger boundaries.
9. **Back-Pressure and Flow Control**: Akka provides means to handle back-pressure scenarios, using streams or patterns 
such as the 'pipe to' pattern.

It's also important to note that while Akka Actors are powerful, they are one tool in a range of options available 
within the Akka toolkit. Other tools include Akka Streams for handling streams of data in a backpressure-aware manner, 
and Akka HTTP for writing reactive, asynchronous HTTP services. These tools can be used in combination to build complex 
distributed systems.

Akka is implemented in Scala, but has a Java API as well, so it can be used in any JVM-based project.

### Pros (apart from the ones already discussed)
- **Concurrent and Distributed by Nature**: Akka actors are excellent for managing concurrency across multiple cores and
for distribution across nodes in a cluster. They can process messages in parallel, and the Akka system can manage
thousands of actors concurrently.
- **Fault Tolerance**: Akka actors can supervise child actors, meaning they can decide what to do if a child actor 
fails. Akka Supervision Strategy and the "let it crash" philosophy provide excellent support for building resilient and 
self-healing systems.
- **Resource Efficiency**: Akka actors are lightweight, allowing millions of actors to exist in a system without 
degrading performance.
- **Message-driven and Reactive**: Akka actors communicate asynchronously using message passing, making the system 
responsive and resilient.

### Cons
- **Learning curve**: There's a steep learning curve due to the change in paradigm when moving from standard OOP to 
actor-based models.
- **Debugging is hard**: Debugging can be tricky in asynchronous systems. Identifying what went wrong and where can be 
challenging due to the asynchronous nature of actor systems.
- **Overkill for simple applications**: If your application does not need high levels of concurrency or does not have to
be distributed, using Akka might be overkill.

### Use Cases
- **Distributed Systems**: Akka actors can be transparently distributed across cluster nodes, making them ideal for 
building distributed systems where workload is shared among many machines.
- **Real-time Processing**: For applications that require real-time data processing, such as gaming servers, analytics 
platforms, or IoT data processing, Akka actors can manage high volumes of messages and concurrent processing.
- **Fault-tolerant Systems**: Akka actors are well-suited to creating systems that can handle failures and recover 
gracefully. This is thanks to the Actor supervision strategy, where parent actors can supervise and decide on the 
recovery mechanism for their child actors.
- **Concurrency Management**: Akka actors can be a great tool for problems where many independent tasks need to be run 
concurrently without blocking, such as web servers, multi-user environments, and simulations.
- **Resource-intensive Applications**: If you have a CPU-bound application, like a complex simulation or computation, 
Akka actors can help evenly distribute work across cores and manage concurrency for maximized throughput.

### Where to avoid
- **Simple CRUD Applications**: For simple, small scale, and monolith applications where there's no significant benefit 
from concurrency management, the asynchronous and non-block nature of Akka is not really required.
- **Microservices Architecture**: If an application is commonly broken down into many independent and loosely coupled 
services communicating through REST APIs, one might prefer a simpler container-based infrastructure rather than dealing 
with actor systems.
- **Tightly Coupled Transactional Systems**: Akka actors are not best suited for managing complex transactional systems 
where multiple operations across diverse resources need to happen atomically. Traditional RDBMS-based systems or newer 
transactional NoSQL databases may better serve such use-case scenarios.