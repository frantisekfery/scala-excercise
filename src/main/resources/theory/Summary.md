# Interview

## Akka Streams
- implementation of Reactive Streams specification
- design for processing and transforming a stream of data
- support back-pressure to ensure that faster producers don't overwhelm slower consumers
- data flow is from source to sink passing through various stages
- then can be flexibly composed to construct complex processing graphs
- support 1 to n transform, 1 to one transforms or strictly one element per event 1 to 1 event driven transform
- type-safety features ease the development of correct systems
- stages can be synchronous or asynchronous, streams can be parallelized and distributed across multiple nodes 
(functions as mapAsync or mapAsyncUnordered — difference is that in the first case paralleled processed records are 
waiting for each other — don't need to manually deal with actors or futures or groupBy dividing stream into sub-streams 
which can be processed independently)
- scaling processing — you can use a poll of actors within the streams. There are several routing strategies — 
RoundRobin, RandomPool, SmallestMailboxPool, BalancingPool — Each strategy affects how messages are distributed across 
the actor pool.
- doesn't support exactly-once out of the box (using 3 strategies for that purpose) -> operation idempotency (with 
timestamps), event deduplication (with IDs, you are checking database or cache, whether arrived event with given ID was
processed - then cleaning cash or database), transactional writes (also with IDs, you are performing more operations in 
one transaction and if either statement fails, the whole transaction won't take an effect)
- high horizontal scalability (in application level, Akka is well-equipped to deal with a high load and can seamlessly 
distribute this across actors and resources)
- best for high-performance streams that are part of the business logic without managing other clusters
- not a good learning curve compared to competitors
- overkill for simple jobs
- less suited for handling truly massive datasets
- in stage, it can be materialized to a network of Akka Actors (Flow.ask function can be used to integrate actors into 
a stream)
- fault tolerance is achieved by tools and mechanism, but handling that is the responsibility of the programmer. There 
are Supervision Strategies as Decider what to do for each kind of exception, it will handle exception and from some can
recover, you can use RestartSource, RestartSink or RestartFlow to automatically restart streams if necessary. Use Graph
Stage Materialization Lifecycle for behavior on stream start, finish, or failure. The resilient of the system depends
on a developer.

## Akka Cluster
- provide fault-tolerant, decentralized, peer-to-peer based cluster with no single point of failure
- it uses gossip protocols and automatic failure detector
- allows application to have on multiple machines (each in one node)
- nodes can join and leave the cluster (this is handled gracefully by Akka Cluster)
- the part of cluster is a collection of the actor systems spread across multiple machines, creating a network of the 
nodes
- resilient, elastic and responsive system
- cluster keeps of all the nodes in the cluster. A node can either be a member of the cluster in various states 
(Joining, Up, Leaving, etc.) or a non-member
- cluster sharding - this is an automatic way of distributing actors across many nodes of an Akka Cluster. Sharding 
helps when you need to track many entities, each with its own persistent state and behavior.
- cluster singleton - if you need to ensure that only one instance of a particular actor is running at any point in the
cluster, Akka provides Singleton actors, this actor is always running on the oldest node.
- cluster routers - akka cluster uses routers for location transparent routing of messages
- cluster pub-sub - model where publisher categorizes messages into topics and sent them to a broker, then subscribers
subscribe to those topics on the same broker.
- distributed data uses Conflict-Free Replicated Data Types, which are designed to allow eventual consistency semantics 
in an efficient manner.

## Akka Actors
- uses model, which is a mathematical model of concurrent computation that treats "actor" as the universal primitives
of concurrent computation
- each actor can execute independently of others
- communication happens only via messages, which arrive into actor mailbox (strongly encapsulated)
- design around a "let it crash" philosophy (fault-tolerant)
- to prevent message loss, a common practice is to use "persistent actors" when an actor receives a command, it 
persists the generated event before processing command and changing its state
- actors can seamlessly be distributed across different nodes and the system is managing routing
- actors are very lightweight concurrency entities, it means that milions of actors can exist in the same system without
impacting performance
- messages are processed in a FIFO manner (first in first out)
- actor process messages sequentially, so there is no need to use lock - it means allowing safe concurrent processing 
and consistent state management
- actors are arranged into tree hierarchies, where parents supervise their children and handle their failures

## Amazon Kinesis Data Streams
- service for collecting and processing large streams of data records in real time
- can handle any amount of streaming data and process data from hundreds to thousands of sources with very low-latency
- can analyze data and get insights in seconds or minutes
- provides multiple way to process data (Lambda, EC2, on-premise)
- its functionality might be limited if you use infrastructure outside AWS
- is fully managed by AWS, this eliminates the operational effort required to set up and maintain the infrastructure
- fault-tolerance is reached by sequence numbers for records that are unique, incremental identifiers assigned to every
record, acting as a watermark. Checkpointing is the mechanism used by Kinesis to handle failures; it logs the sequence 
number of the last record processed to a DynamoDB table, effectively providing a save point in the stream that 
a consumer can return to on failure. On recovery from a failure, the consumer can start processing from the last 
successful checkpoint, reprocess records, and ensure no data loss, providing at least once processing semantics. How 
frequently this tracking is done is typically driven by the trade-off between fault tolerance and performance.
- competitors are Apache Kafka, Google Pub/Sub and Azure Event Hubs from Microsoft
- to use Kinesis with Akka Streams, you can use Kinesis Client Library, which offers a high-level API for easy
management of data records stored in Kinesis; it handles complexities such as load-balancing of streams, coordination
between distributed services, adaptive response to changes in stream volume and fault tolerance
- there is another library for Java and Scala — Alpakka, built on top of Akka Streams. It provides Reactive Streams
compliant connectors to various technologies, including AWS Kinesis Data Streams — this means you can use Alpakka as an
interface between Akka Streams and Kinesis. Basically, it allows you to treat Kinesis as a Source or Sink for
your Akka Streams

## Databases
Database is a place where are store structured set of data, and it enables efficient access to that data.

### Vertical Scaling
In vertical scaling, the more hardware resources such as memory, storage, or computational power is added to an existing
database server. This is quite safe.

### Horizontal Scaling
In horizontal scaling, the load is distributed across multiple servers. This is quite challenging, because the dataset
is spread across different machines. There are two main techniques for how this is achieved — Sharding and Replicating.

#### Sharding
- this is done by sharding - dataset is decided into smaller parts (shards) and each shard is put on a different server 
- it is more complex to implement as it involves handling data distribution
- pros:
  - improved performance — if implemented correctly, finding t-shirt in small box vs. searching in big closet
  - high availability — if one shard goes down, it won't affect the entire system
  - scalability — sharding allows you to linearly scale your database system by adding more servers (adding more boxes 
  into closet), 
  - geographical distribution — it allows you to store data closer to your customers. This can help reduce latency
- cons
  - increased complexity — it involves delicate partitioning, maintaining data consistency across shards, more
  complex strategies for queries and transactions that span multiple shards
  - data repartition - sharding strategy must be thought out carefully in advance, considering data growth and how data
  will be accessed. If the sharding key is poorly chose, you may face an expensive operation to repartition your data 
  later
  - complicated queries — if you need to join or aggregate data from multiple shards - harder to find specific outfit 
  from different boxes
  - increased management overhead — overhead of managing multiple shards, maintaining consistency across them, handling
  failures
- use cases suitable for sharding are large scale applications, high traffic systems, multi-tenant systems - also you
reach data isolation, geographically distributed applications
- use cases not suited for sharding small-scale applications, complex transactions, frequent inter-shard operations, 
lack of a clear sharding key

#### Replication
- the entire database is copied into multiple servers; in case of failure, the system can switch to a backup server
- more strategies — master and slave replication and multi-master replications
- pros:
  - high availability — if one database server goes down, the system can immediately switch to a replicated server with
  no data loss 
  - load distribution — you can distribute read queries across replicas to reduce a load on a single server and increase
  performance
  - data security — replicas act as backups, in case of a catastrophic event, data loss can be minimized or avoided 
  entirely
  - geographical distribution — if application serves users globally, situating replicas in different locations can
  reduce latency and improve access speed
- cons:
  - write overhead — every write operation must be carried out on every replica, which can increase latency for 
  write operations
  - data consistency — there can be a delay in propagation of updates that may result in temporary inconsistencies
  between replicas (also known as eventual consistency)
  - storage cost — each replica will consume the same amount of storage as the original database, so costs can increase
  considerably
  - complexity - implementing replication, managing multiple replicas, handling failures, resolving conflicts, can add
  complexity to the system

#### Consistent Hashing
- commonly used in distributed database systems to solve a problem of data distribution across multiple nodes. This is 
especially crucial when the database needs to be scaled up or down
- comparing to traditional hashing, after adding or removing node all keys must be remapped vs. in consistent hashing
when slot is added or removed, only the keys assigned to that slot must be remapped
- it looks like a clock with hours, so the new record key is hashed and chose in which hour is stored. The same when
I want to fetch data, the database again hashes my key to know where is the record stored
- databases using consistent hashing: Cassandra, Riak, DynamoDB
- traditional SQL databases like Postgres, MySQL generally don't use consistent hashing

#### CAP Theorem
- it is a concept that a distributed computing system is unable to simultaneously provide all three of the following 
guarantees: consistency (C), availability (A), partition tolerance (P)
- helps to understand trade-offs between availability and consistency when network partition occurs
- when failure of part of the system (partition tolerance) occurs and a system wants to be functional, it needs to make 
a decision what priority is higher — to be consistent or available.

## Distributed Systems
- microservice architecture - loosely coupled services, each service represents specific business capability
- message-driven architecture - trigger for computation is a message - user actions, sensor outputs, or message from
another program, so application are designed to react promptly to them, in contrast to event-driven, the producers
of the message know who should handle this message and address message to this part of the component. There is used 
message queue
- api-driven architecture - API is the only point where interaction with app can be made, API exposes the functionality 
of the app to the world, API encapsulates the business logic and database transactions. Web apps, mobile apps or other
services can use these API to access the functionality they provide
- event-driven architecture - a software component (producer) emits an event when a specific task or state change occurs
without knowing who is going to use or handle that event, consumers that are interested in these events will listen and
react to them, producers and consumers are completely decoupled which allows for high scalability and flexibility

### Transaction writes
Transactional writes are used in a variety of distributed systems contexts beyond just databases. If a system operation
involves multiple steps, and it's important that either all or none of these steps are carried out (transaction 
rollback), then that operation is a good candidate for a transactional approach.

**Use-cases:**
- Transaction writing into replicas in distributed file systems or databases. 
- In message queues, a transaction write might consist of receiving a message, processing it and acknowledging it
- In microservices, one high-level operation can involve multiple services with partial operations 
- In a distributed cache (like Redis), transaction ensures that all read-write operations happen atomically to avoid any 
- In Consensus protocols, transactions often involve multi-round voting processes to ensure that every node in the 
cluster agrees with some change, and that the system as a whole continues operating with a consistent view on its state.

### Serialization
- process of converting an object into a format that can be transmitted across a network or store in some persistent
storage, like a file or a database
- it allows exchanging complex data structures between different parts of a system, irrespective of their platform or 
language
- performance — some serialization formats like Protocol Buffers or Avro are more compact and faster than others, making 
them a good fit for systems where performance is a key concern
- a good practice is to always use libraries for serialization/deserialization and prefer serializing into 
non-executable formats (JSON, XML or a binary format like Protocol Buffers (protobuf)) and ensure data is sent over 
secure channels

### Locks
Lock is a mechanism that helps ensure the integrity of data in concurrent programming. So one thread can modify 
the resource at a time. If one thread needs to lock more resources, then deadlock can occur. Deadlock is when one thread
locked resource A and want to lock resource B, but resource B is locked by another thread which want to lock resource A.
Both threads are waiting for another to release its resource.

**How to avoid deadlock:**
- Assign and order to the resources and ensure that all processes request resources in that order
- Use timeouts — when a process requests a resource, instead of allowing it to wait indefinitely, there will be use
timeout, and after the limit process aborts and releases all its held locks and then we can reply it.

## Future, Promise and Callback

### Callback
Callbacks are functions that are passed as parameters to other functions and are invoked after certain actions have been
completed. It is for handling completion of asynchronous tasks. It can lead to callback hell, which is complex nested
code that becomes hard to maintain.

### Future
A Future is a read-only placeholder object for a value that may not yet exist. It is a way to represent a value that is 
initially unknown but becomes fulfilled at some point, with the value it will contain.

**Methods**:
map (chain function), flatMap(get rid of nested future), filter (will return future with value or exception), recover
and recoverWith (when the future is a failure, it creates a new future by applying a function), foreach (use to perform 
function when future completes successfully), onComplete (perform side effect once the Future is completed), andThen
(perform side effect - logging and then return original future), transform and transformWith (like a map used in both
cases - success and failure), zip (pair two futures, returning a new future holding tuple with the result of both
original futures, if they are both successful), fallbackTo (creates a new Future if original future failed), failed 
(if future failed it returns a new future holding the exception as Success - changing from Future[T] to 
Future[Throwable], in case of success, new future fails with NoSuchElementException), zipWith (combines two futures 
using a combining function)

### Promise
Promise in Scala is a writable container, which completes the Future. The term "completes a Future" means that a Promise 
in Scala provides a way to control when and how a Future will get its result. Promise as a controller for the Future. 
A Promise is an object that can complete a Future with a result. When you create a Promise, it includes a corresponding
Future. You can complete the Promise by either successfully providing a result or failing with an exception.

**Use-cases for Future and Promise**:
1) **Bridge Over Non-Futures Supported APIs**: This comes into play when APIs do not natively support Futures. These are
usually callback-based or blocking APIs. You can leverage promises to convert these APIs into a Future-based API. 
2) **Parallel Computations**: This makes it possible to carry out several computations in parallel, creating a Future
for each, and then combining their results as soon as they are available. With Future, you can write higher level
operations like map, filter, and reduce, which can handle the thread management for you.
3) **Timeouts**: You can use Promise for setting up a timeout for a Future's operation.
4) **Communicating Between Threads**: Promises can be used as a one-time communication mechanism between different
threads, particularly to communicate the result of a computation carried out by a worker thread to another thread.

### Difference
Scala uses the Future and Promise construct where the Future is a read-only placeholder view of a value that may not yet 
exist because it's being computed or retrieved asynchronously. Promise in Scala is a writable, single-assignment 
container, which completes the Future. In other words, the Future represents a value that will be available in 
the future, while Promise is a way to create and complete a Future.

## Execution

- **Synchronous** — operations are done one at a time, and an operation must be completed before the next one starts.
The execution is blocked until the operation finished. This is typically seen in single-threaded environment.
- **Asynchronous** — operations are started then set aside as other operations are begun prior to the previous ones 
finishing. The execution is not blocked; it continues with the next operation regardless of whether the previous one has 
completed. This occurs notably in multithreaded environments, or in single-threaded environments that use event-driven
programming or callbacks.
- **Sequential** — it means that operations are executed in sequence.
- **Concurrency** — concurrency means dealing with multiple tasks at the same time but not necessarily simultaneously. 
Single CPU is switching between tasks, creating an illusion of parallel execution. One cook preparing multiple dishes.
- **Parallelism** — the operation execution is done truly simultaneously, which implies having multiple CPUs. Multiple 
cooks each preparing a different dish. 
