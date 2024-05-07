# Streams

## Libraries
1) Akka Streams
2) Spark Streaming
3) Kafka Streams

### Common features
1. High throughput
2. Scalable 
3. Fault Tolerant 
4. Concurrent

## Kafka Streams
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

### Strengths
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
- Best for high-performance streams outside your microservice's logic which use the same mesage bus and low-latency

### Weaknesses
- Uses procedural Java-style API.
- Requires a separate Kafka Cluster with its own management (not necessarily on separate physical machines).
- Needs comprehensive configuration knowledge.
- Supports only producer-consumer type architectures.

## Akka Streams
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

Akka Streams doesn't support out of the box exactly-once feature. There are 3 strategies how to achieve it in application
level. Try to make your operation idempotent (with timestamps), event de-duplication (with IDs, you are checking 
database or cache, whether arrived event with given ID was processed, you have to take care of properly cleaning cash or
database), transactional writes (also with IDs, you are writing into database more inserts in one transaction and if
either statement fails, whole transaction won't take an effect).

### Strengths
- Back-pressure feature ensures data integrity and system resilience
- Flexibly composable stages allow for complex, custom data processing graphs
- Strong type-safety eases the development of correct systems
- Concurrency and distribution capabilities make it powerful for large-scale systems
- Provides Java and Scala APIs for broad accessibility
- High scalability and fault-tolerance. The scalability of Akka streams is more focused on the application level.
Within a distributed or concurrent application environment, Akka is well-equipped to deal with high load and can 
seamlessly distribute this across actors and resources
- Best for high-performance streams that are part of the business logic with low-latency without managing other clusters

### Weaknesses
- Steeper learning curve compared to some other Stream processing libraries due to its powerful feature set and unique 
terminology
- It may be overkill for simple jobs due to its complexity
- As part of the larger Akka toolkit, it is intertwined with other Akka libraries, which can make it heavy-weight,
especially if only the stream processing functionality is needed
- Less suited for handling truly massive datasets

### Actors in Akka Streams
When an Akka Stream run, it is materialized to a network of Akka Actors that handle the data processing. Akka Stream
stages become Actors when the stream starts running. However, you do not have to directly interact with these actors
when defining your stream processing logic. Flow.ask function can be used to integrate actors into a stream, for
delegating some processing to the actors.

### Parallelism and Concurrency in Streams
Akka Streams has operators like **mapAsync** and **mapAsyncUnordered** which provide a way to handle computations in
parallel without needing to manually deal with actors or futures. The operator **groupBy** allows dividing a stream into
substreams which can be processed independently, providing another angle for concurrent processing.

### Scaling Processing with Akka Actors
For extensive computational workloads, you can utilize a pool of actors within the streams. Akka provides several
routing strategies, including RoundRobinPool, RandomPool, SmallestMailboxPool, and BalancingPool. Each strategy affects
how messages are distributed across the actor pool.

### Routing Strategies
1) **RoundRobinPool**: Works best when each task requires roughly the same amount of processing time.
2) **RandomPool**: Randomly selects an actor for each message, useful when routing cost is much higher than processing.
3) **BalancingPool**: Works best with tasks of vastly different processing times, and aims to keep all actors busy by 
routing new tasks to idle actors. The caveat is that it assumes all actors are located on the same node.
4) **SmallestMailboxPool**: Is useful when the actors are approximately equally fast at processing messages. It routes 
new messages to the actor that has the fewest messages waiting in its mailbox.

## Spark Streaming
Spark Streaming is an extension of the core Spark API that allows scalable, high-throughput, and fault-tolerant stream 
processing of live data streams. It can ingest data from sources like Kafka, Akka, Amazon Kinesis, and more in real 
time.

Data is processed in micro-batches rather than individual records, which are processed using Spark's core engine to 
distribute across a cluster. This approach makes Spark Streaming a micro-batch processing system.

The 'DStreams' model, short for Discretized Streams, forms the basic building block of the Spark Streaming library. 
A DStream can be defined as a sequence of RDDs (Resilient Distributed Datasets), which are the primary data abstraction 
in Spark. These RDDs can be processed using transformations and actions provided by Spark Core.

### Strengths
- High-level functions such as map, reduce, join, window, and more
- Provides exactly-once processing semantics with powerful capabilities through Spark Core
- Scales easily with the number of nodes in the cluster, providing linear scalability
- High-throughput and fault-tolerant capabilities
- APIs provided in multiple languages such as Java, Scala, and Python
- Provides seamless integration with other Spark tools like MLlib and GraphX for machine learning and graph processing
- Best for unbounded big data computations

### Weaknesses
- Loss of type safety in the DataFrame/SQL API
- As a micro-batch processing system, Spark Streaming cannot provide true real-time processing; the latency is tied to 
the duration of the micro-batches
- Complex and custom operations are harder to implement
- Requires a relatively deep understanding of the entire Spark ecosystem due to its tight integration with Spark Core
- Needs dedicated cluster
- Bad for low latency/real-time system

### Spark Streaming Resilience
Spark Streaming is designed to be a fault-tolerant system. When dealing with the failure of worker nodes, it uses the 
lineage information of RDDs to recover the lost data.

### Spark Streaming High Availability
Spark Streaming can recover from failure of the driver node as well, although this feature needs to be explicitly 
enabled. To support driver node recovery, Spark uses a write-ahead log design to save the received data into 
a fault-tolerant storage system, which helps ensure that no data is lost.

### Spark Streaming in Cluster Managers
Spark Streaming applications can be run on different cluster managers supported by Spark such as Hadoop YARN, Mesos, 
Kubernetes, or its standalone cluster manager.
