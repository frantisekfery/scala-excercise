# Scala Learning

This project is created for learning scala purpose.

## Streams
Stream processing distributes computation over large volumes of high-velocity, continuously-generated data—datasets that
are too large for traditional databases.

### Libraries

1) Akka Streams
2) Spark Streaming
3) Kafka Streams
4) Apache Flink

#### Common features
1. High throughput
2. Scalable 
3. Fault Tolerant 
4. Concurrent

### Kafka Streams

Kafka Streams is a client library designed for building mission-critical real-time applications and microservices, with
the input and output data stored in Kafka clusters for processing unbounded data. It includes multiple producers
enqueueing records and multiple consumers reading from various points in the topics.

Records from the Kafka cluster are stored as key-value pairs in binary. These pairs are serialized and
deserialized using Serde. Records from the stream are transformed into a table, reflecting Kafka's stream-table duality.

In this context, a stream is an unbounded, continuously updating sequence of records. Each record represents an
event occurrence, and streams are ordered, replayable, and fault-tolerant. Conversely, a table represents a
stream viewed at a specific point in time. It mirrors the current state at any given moment, and whenever a new event
occurs in the stream, the table updates to reflect the most recent state.

This duality implies that streams and tables can be seamlessly converted or interpreted as one another. Specifically,
a stream-to-table transformation is performed by aggregating the stream, while a table-to-stream transformation is
achieved by capturing changes to the table. The latter results in a "changelog" stream, which can then be written to
another topic. The entire process is initiated with `streams.start()`.

Topic 'T' has three partitions: P1, P2, P3; and three Kafka brokers: K1, K2, K3 and three consumer groups (Group A, 
Group B and Group C) each have two consumers. With partitioning, Kafka allows for horizontal scalability. Your 
application can handle increased workload without compromising performance. By replicating the partitions across 
different brokers, Kafka provides fault-tolerance. Even when there are broker failures, data is still available and 
reliably accessible by switching to the replica.

**Setup:**
P1 is hosted on K1, replicated on K2, and consumed by consumers A1 and A2 from Group A
P2 is hosted on K2, replicated on K3, and consumed by consumers B1 and B2 from Group B
P3 is hosted on K3, replicated on K1, and consumed by consumers C1 and C2 from Group C

This maximizes **parallel processing**, as consumers from each group processes data independently of one partition, 
allowing the system to efficiently handle increased workloads.

#### Strengths

- Designed as a library for Apache Kafka to process unbounded data.
- Enables robust stream processing, such as windowed aggregations and data stream joins, without a separate cluster
  requirement.
- Elastic, horizontally scalable (workload is split into partitions and distributed across the cluster nodes), and 
fault-tolerant (replication feature, data is safely stored in and retrieved from Kafka, including situations when 
a Streams application instance fails)
- Supports exactly-once message transmission mechanism.
- The KS API allows a Java/Scala application to interact with a Kafka Cluster.
- Multiple applications can interact with the same cluster.
- The architecture promotes the use of microservices.

#### Weaknesses

- Uses procedural Java-style API.
- Requires a separate Kafka Cluster with its own management (not necessarily on separate physical machines).
- Needs comprehensive configuration knowledge.
- Supports only producer-consumer type architectures.

### Akka Streams

Akka Streams is a powerful implementation of the Reactive Streams specification, designed for processing and 
transforming a stream of data. It's developed on top of the Akka toolkit. It employs back-pressure to ensure that faster 
producers don't overwhelm slower consumers.

In Akka Streams, data flows from the source to the sink, passing through various stages (operations). Each source, sink, 
or stage is a self-contained piece of processing logic, and they can be flexibly composed to construct complex 
processing graphs.

These stages can be synchronous or asynchronous, and they support emitting zero or more elements for each input element 
(1-to-n transforms), strictly one output element per input element (1-to-1 transforms), or strictly one element per 
event (1-to-1 event-driven transforms).

Akka Streams has strong type-safety features, which makes it easier to construct correct systems. It's also built with 
an eye towards concurrency and distribution, as streams can be parallelized and distributed across multiple nodes.

#### Strengths

- Back-pressure feature ensures data integrity and system resilience.
- Flexibly composable stages allow for complex, custom data processing graphs.
- Strong type-safety eases the development of correct systems.
- Concurrency and distribution capabilities make it powerful for large-scale systems.
- Provides Java and Scala APIs for broad accessibility.
- High scalability and fault-tolerance. The scalability of Akka streams is more focused on the application level.
Within a distributed or concurrent application environment, Akka is well-equipped to deal with high load and can 
seamlessly distribute this across actors and resources.

#### Weaknesses

- Steeper learning curve compared to some other Stream processing libraries due to its powerful feature set and unique 
terminology.
- It may be overkill for simple jobs due to its complexity.
- As part of the larger Akka toolkit, it is intertwined with other Akka libraries, which can make it heavy-weight,
especially if only the stream processing functionality is needed.
- Less suited for handling truly massive datasets

#### Actors in Akka Streams
When an Akka Stream run, it is materialized to a network of Akka Actors that handle the data processing. Akka Stream
stages become Actors when the stream starts running. However, you do not have to directly interact with these actors
when defining your stream processing logic. Flow.ask function can be used to integrate actors into a stream, for
delegating some processing to the actors.

#### Parallelism and Concurrency in Streams
Akka Streams has operators like **mapAsync** and **mapAsyncUnordered** which provide a way to handle computations in
parallel without needing to manually deal with actors or futures. The operator **groupBy** allows dividing a stream into
substreams which can be processed independently, providing another angle for concurrent processing.

#### Scaling Processing with Akka Actors
For extensive computational workloads, you can utilize a pool of actors within the streams. Akka provides several
routing strategies, including RoundRobinPool, RandomPool, SmallestMailboxPool, and BalancingPool. Each strategy affects
how messages are distributed across the actor pool.

#### Routing Strategies
1) **RoundRobinPool**: Works best when each task requires roughly the same amount of processing time.
2) **RandomPool**: Randomly selects an actor for each message, useful when routing cost is much higher than processing.
3) **BalancingPool**: Works best with tasks of vastly different processing times, and aims to keep all actors busy by 
routing new tasks to idle actors. The caveat is that it assumes all actors are located on the same node.
4) **SmallestMailboxPool**: Is useful when the actors are approximately equally fast at processing messages. It routes 
new messages to the actor that has the fewest messages waiting in its mailbox.

#### Spark Streaming
Spark Streaming is an extension of the core Spark API that enables scalable, high-throughput, fault-tolerant stream 
processing of live data streams. Data can be ingested from many sources like Kafka, Flume, Kinesis, or TCP sockets,
and can be processed using complex algorithms expressed with high-level functions like map, reduce, join and window. 
Finally, processed data can be pushed out to file systems, databases, and live dashboards. However, unlike Akka Streams, 
its focus is on processing big data, and it lacks backpressure concept.

### Akka Streams
Akka Streams is a library within the Akka platform that handles streaming data in applications. It’s designed to control
the flow of large amounts of data so it doesn’t overwhelm the memory of your system.


Retry: Retry suggests the reattempts operations in case of failures. For instance, if you are making a network request 
in an Akka stream and that operation fails, you may want to retry that operation a fixed number of times before giving
up.

Kinesis / Kafka: Both Kinesis and Kafka are high-throughput, distributed data streaming platforms used for real-time 
ingestion of data.

Commit: 'Committing' is a way of confirming that a particular operation or set of operations were successful.

Checkpoint: A 'checkpoint' in stream processing is the process of saving the state of a stream at specific intervals to
enable rollback or recovery in case of failures. This is important for ensuring exactly-once semantics.

Message processing semantics: When processing messages, how you handle them can be crucial to the behavior of your
application. There are three primary modes: 'at least once', where each message is processed one or more times; 
'at most once', where each message is processed no more than once and messages may get lost; and 'exactly once', where 
each message is processed precisely once—which is a more complex mode that usually involves transactions or
deduplication.

Database Scaling: Scaling databases means adjusting to accommodate more traffic. This can be horizontal scaling (adding 
more servers) or vertical scaling (upgrading the capabilities of an existing server).

Serializer for Distributed System: Serializers convert objects into a format that can be transferred over the network 
(or stored on disk), then convert them back into objects. In a distributed system like Akka or Kafka, you need efficient
serializers for low latency and less network usage. Here are some:
1) Protocol Buffers (protobuf): need to have schemas
2) Apache Avro (avro4s)
3) JSON (json4s and Circle)

Akka Cluster: Akka Cluster provides a fault-tolerant decentralized peer-to-peer based cluster membership service with no 
single-point-of-failure or single point of bottleneck. It does this using gossip protocols and an automatic failure 
detector.

Akka Persistence: Akka Persistence provides easy-to-use APIs for saving the state of actors. The state of an actor is 
typically the past received messages (events) and current state derived from these events. Persisting actor state 
enables state recovery after JVM crashes or restarts, and also enables actors to move to different nodes in a cluster.

Persistence store: The persistence store, as used by Akka Persistence, is the database where actor states/events are
saved. This could be a traditional SQL database, a NoSQL database like Cassandra, or a file system.

Event sourcing via Akka Persistence: Event sourcing is the practice of saving every state-changing event of an 
application. This allows complete "replay" of an application's state by simply rerunning all events. With Akka 
Persistence, you can achieve event sourcing by saving every received message with the persist function.



Each scala object in package [excercise](src/main/scala/excercise) is executable.