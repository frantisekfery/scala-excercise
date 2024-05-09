# Interview

## Streams

### Kafka Streams
- for processing bounded or unbounded data
- multiple processor enqueueing records
- multiple consumers reading records
- data are divided into topics
- records are key value pairs in binary (Serde serializer)
- records are transformed into table (stream-table duality)
- each record is event
- streams are ordered, repayable and fault-tolerant
- topic can have partitions
- broker is a server that persist messages into topics
- there are more broker, they are stateless
- with partitioning Kafka allows horizontal scalability
- vertical by adding more ram, rom and memory
- elastic, horizontally scalable (workload is split into partitions and distributed across the cluster nodes)
- fault-tolerant (replication feature, data is safely stored in and retrieved from Kafka, including situations when a 
Streams application instance fails
- Supports exactly-once message transmission mechanism out of the box
- multiple application can interact with the same cluster
- best for high-performance streams outside the application
- need comprehensive configuration knowledge, required own management
- supports only producer-consumer type architecture

### Akka Streams
- implementation of Reactive Streams specification
- design for processing and transforming a stream of data
- support back-pressure to ensure that faster producers don't overwhelm slower consumers
- data flow is from source to sink passing through various stages
- then can be flexibly composed to construct complex processing graphs
- support 1 to n transform, 1 to 1 transforms or strictly one element per event 1 to 1 event driven transform
- type-safety features eases the development of correct systems
- stages can be synchronous or asynchronous, streams can be parallelized and distributed across multiple nodes 
(functions as mapAsync or mapAsyncUnordered - difference is that in first case paralleled processed records are waiting 
each other - don't need to manually deal with actors or futures or groupBy dividing stream into sub-streams which can be 
processed independently)
- scaling processing - you can utilize a poll of actors within the streams. and there are several routing strategies - 
RoundRobin, RandomPool, SmallestMailboxPool, BalancingPool - each strategy affects how messages are distributed across 
the actor pool
- doesn't support exactly-once out of the box (using 3 strategies for that purpose) -> operation idempotency (with 
timestamps), event deduplication (with IDs, you are checking database or cache, whether arrived event with given ID was
processed - then cleaning cash or database), transactional writes (also with IDs, you are writing into database more 
inserts in one transaction and if either statement fails, whole transaction won't take an effect - it is applicable if 
database supports transactions)
- high horizontal scalability (in application level, Akka is well-equipped to deal with high load and can seamlessly 
distribute this across actors and resources)
- best for high-performance streams that are part of the business logic without managing other clusters
- not good learning curve compared to competitors
- overkill for simple jobs
- less suited for handling truly massive datasets
- in stage, it can be materialized to a network of Akka Actors (Flow.ask function can be used to integrate actors into 
a stream)

## Akka Cluster
- provide fault-tolerant, decentralized, peer to peer based cluster with no single point of failure
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
- cluster pub-sub - model where publisher categorize messages into topics and sent them to a broker, then subscribers
subscribe to those topics on the same broker.
- distributed data uses Conflict-Free Replicated Data Types which are designed to allow eventual consistency semantics 
in an efficient manner.

## Amazon Kinesis Data Streams
- service for collecting and processing large streams of data records in the real time
- can handle any amount of streaming data and process data from hundreds to thousands of sources with very low-latency
- can analyze data and get insights in seconds or minutes
- provides multiple way to process data (Lambda, EC2, on-premise)
- its functionality might be limited if you use infrastructure outside of AWS
- is fully managed by AWS, this eliminates the operational effort required to set up and maintain the infrastructure
- fault-tolerance is reached by sequence numbers for records that are unique, incremental identifiers assigned to every
record, acting as a watermark. Checkpointing is the mechanism used by Kinesis to handle failures, it logs the sequence 
number of the last record processed to a DynamoDB table, effectively providing a save point in the stream that 
a consumer can return to on failure. On recovery from a failure, the consumer can start processing from the last 
successful checkpoint, reprocess records, and ensure no data loss, providing at least once processing semantics. How 
frequently this tracking is done is typically driven by the trade-off between fault tolerance and performance.
- competitors are Apache Kafka, Google Pub/Sub and Azure Event Hubs from Microsoft
- to using Kinesis with Akka Streams, you can use Kinesis Client Library, which offers a high level API for easy
management of data records stored in Kinesis, it handles complexities such as load balancing of streams, coordination
between distributed services, adaptive response to changes in stream volume and fault tolerance
- there is another library for Java and Scala - Alpakka, built on top of Akka Streams. It provides Reactive Streams
compliant connectors to various technologies, including AWS Kinesis Data Streams - this means you can use Alpakka as an
interface between Akka Streams and Kinesis -0 basically it gives you an ability to treat Kinesis as a Source or Sink for
your Akka Streams

## Databases
- database is place where are store structured set of data, and it enables efficient access to that data
- there are two main techniques when database need to be scaled when bigger loads is causing lowering the performance -
vertical scaling - more hardware resources such as memory, storage, or computational power to an existing database
server or horizontal scaling the load is distributed across multiple servers. The first one is quite safe, but the
second way is quite challenging, because the dataset is spread across different machines. 

### Horizontal Scaling

#### Sharding
- this is done by sharding - dataset is decided into smaller parts (shards) and each shard is put on a different server 
- it is more complex to implement as it involves handling data distribution
- pros:
  - improved performance - if implemented correctly, finding t-shirt in small box vs searching in big closet
  - high availability - if one shard goes down, it won't affect the entire system
  - scalability - sharding allows you to linearly scale your database system by adding more servers (adding more boxes 
  into closet), 
  - geographical distribution - allows you to store data closer to your customers. This can help reduce latency
- cons
  - increased complexity - it involves delicate partitioning, maintaining data consistency across shards, more
  complex strategies for queries and transactions that span multiple shards
  - data repartition - sharding strategy must be thought out carefully in advance, considering data growth and how data
  will be accessed. If the sharding key is poorly chose, you may face an expensive operation to repartition your data 
  later
  - complicated queries - if you need to join or aggregate data from multiple shards - harder to find specific outfit 
  from different boxes
  - increased management overhead - overhead of managing multiple shards, maintaining consistency across them, handling
  failures
- use cases suitable for sharding are large scale applications, high traffic systems, multi-tenant systems - also you
reach data isolation, geographically distributed applications
- use cases not suited for sharding small scale applications, complex transactions, frequent inter-shard operations, 
lack of clear sharding key

#### Replication
- the entire database is copied into multiple servers, in case of failure, the system can switch to a backup server
- more strategies - master and slave replication and multi-master replications
- pros:
  - high availability - if one database server goes down, the system can immediately switch to a replicated server with
  no data loss
  - load distribution - you can distribute read queries across replicas to reduce load on a single server and increase
  performance
  - data security - replicas act as a backups, in case of a catastrophic event data loss can be minimized or avoided 
  entirely
  - geographical distribution - if application serves users globally, situating replicas in different locations can
  reduce latency and improved access speed
- cons:
  - write overhead - every write operation must be carried out on every replica, which can increase latency for 
  write operations
  - data consistency - there can be a delay in propagation of updates which may result in temporary inconsistencies
  between replicas (also known as eventual consistency)
  - storage cost - each replica will consume the same amount of storage as the original database, so costs can increase
  considerably
  - complexity - implementing replication, managing multiple replicas, handling failures, resolving conflicts, can add
  complexity to the system

#### Consistency Hashing
- commonly used in distributed database systems to solve problem of data distribution across multiple nodes. This is 
especially crucial when the database needs to be scaled up or down
- comparing to traditional hashing, after adding or removing node all keys must be remapped vs in consistent hashing
when slot is added or removed, only the keys that were assign to that slot must be remapped
- it looks like a clock with hours, so the new record key is hashed and chose where in which hour is stored then, when
I want to fetch data, the database again hashed my key to know where is the record stored
- databases using consistent hashing: Cassandra, Riak, DynamoDB
- traditional SQL databases like Postgres, MySQL generally don't use consistent hashing

#### CAP Theorem
- it is a concept that a distributed computing system is unable to simultaneously provide all three of the following 
guarantees: consistency (C), availability (A), partition tolerance (P)
- helps to understand trade-offs between availability and consistency when network partition occurs
- when failure of part of the system (partition tolerance) occurs and system want to be functional it needs to make 
a decision what priority is higher - to be consistent or available.

## Distributed Systems
- microservice architecture - loosely coupled services, each service represents specific business capability
- message-driven architecture - trigger for computation is a message - user actions, sensor outputs, or message from
another program, so application are designed to react promptly to them, in contrast to event-driven, the producers
of the message know who should handle this message and address message to this part of component. There is used message 
queue
- api-driven architecture - API is the only point where interaction with app can be made, API expose the functionality 
of the app to the world, API encapsulate the business logic and database transactions. Web apps, mobile apps or other
services can use these API to access the functionality they provide
- event-driven architecture - a software component (producer) emits an event when a specific task or state change occurs
without knowing who is going to use or handle that event, consumers that are interested in these events will listen and
react to them, producers and consumers are completely decoupled which allows for high scalability and flexibility

### Serialization
- process of converting an object into a format that can be transmitted across a network or store in some persistent
storage, like a file or a database
- it allows to exchange complex data structures between different parts of a system, irrespective of their platform or 
language
- performance - some serialization formats like Protocol Buffers or Avro are more compact and faster than others, making 
them a good fit for systems where performance is a key concern
- a good practice is to always use libraries for serialization/deserialization and prefer serializing into 
non-executable formats (JSON, XML or a binary format like Protocol Buffers (protobuf)) and ensure data is sent over 
secure channels