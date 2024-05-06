# Databases
Database scaling is an important strategy in managing larger database loads for maintaining responsiveness and 
optimizing performance. It generally involves two main techniques: vertical scaling (also called "**scaling up**") and 
horizontal scaling (also called "**scaling out**").

## Vertical Scaling
This method involves adding **more hardware resources such as memory, storage, or computational power to an existing 
database server.** It's typically easier to implement as it involves upgrading a single component. However, there's an 
upper limit to the capacity that can be added to a single server, making this strategy not always suitable for extremely 
large datasets or very high traffic applications.

## Horizontal Scaling
Here, **the load is distributed across multiple servers**. In this strategy, the data is spread across 
different databank machines, reducing the load on a single system and increasing the performance.

In the context of databases, this is often accomplished in one of two ways:

## Sharding
**The dataset is broken into smaller parts ("shards"), and each shard is put on a different server.** Each shard 
handles its own transactions and has its own resources. It's more complex to implement as it involves handling data 
distribution and requires proper design to avoid data hotspots.

### Pros (Benefits)
1) **Improved Performance**: If implemented correctly, sharding can significantly reduce query response times by 
distributing the load across multiple servers, decreasing the amount of data each server has to manage and the number of 
simultaneous requests they handle. **Faster Searches:** Like finding your favorite shirt in a small box quickly instead
of searching through the big, messy closet.
2) **High Availability**: If one shard (server) goes down, it won't affect the entire system, only the subset of the
data in that shard. Remaining shards continue to function, which increases your system availability. **Less Crashing**: 
If one box falls (a server goes down), the others are still okay and you can still get the other shirts you need from 
those.
3) **Scalability**: Sharding allows you to linearly scale your database system by adding more servers. This helps to 
keep up with data growth over time. **Add More Boxes**: If you have more clothes (data), you can just add more boxes 
(servers).
4) **Geographical Distribution**: Sharding allows you to store data closer to where customers are located. This can help 
in reducing query latency and adhering to local regulations regarding data sovereignty. **Sort by Location**: You can 
put the boxes (servers) in different rooms (locations) closer to where you need the clothes (data) most.

### Cons (Challenges)
1) **Increased Complexity**: Sharding a database is a complex process. It involves delicate partitioning, maintaining 
data consistency across shards, and more complex strategies for queries and transactions that span multiple shards. 
**More Complex**: Sorting and maintaining the clothes into different boxes (sharding a database) is hard work.
2) **Data Repartition**: Sharding strategy must be thought out carefully in advance, considering data growth and how 
data will be accessed. If the sharding key is poorly chosen, you may face an expensive operation to repartition your 
data later. **Best Ways to Sort**: You have to figure out the best way to sort the clothes into boxes (data into 
servers) from the start, or you'll have a hard time later.
3) **Complicated Queries**: Some queries that used to be straightforward become complicated or slower, if they need to 
join or aggregate data from multiple shards. **Harder to Find Specific Outfit**: Finding an outfit that uses pieces from 
different boxes (a query requiring data from multiple shards) is harder and slower.
4) **Increased Management Overhead**: Sharding comes with the overhead of managing multiple shards, maintaining
consistency across them, handling failures, etc. **More Boxes, More Work**: You now have multiple boxes (servers) to 
keep tidy instead of one.

### Use cases suitable for Sharding
1) **Large Scale Applications**: If you're handling a huge amount of data that a single database instance is struggling
to contain or manage, sharding may be a good option. It can distribute your data across multiple servers, drawing upon 
their collective resources. 
2) **High Traffic Systems**: For applications that serve an extremely large number of users and transactions, sharding 
can help distribute the workload and reduce the pressure on any single server. 
3) **Multi-Tenant Systems**: In multi-tenant architectures where each tenant's data is isolated and doesn't frequently 
interact with others, sharding is quite beneficial. Each tenant's data can live on a separate shard, reducing 
cross-tenant activity.
4) **Geographically Distributed Applications**: If you have users all over the world and latency is a concern, you might
shard your database by geography, keeping data closer to the users which will access it.

### Use Cases Where Sharding May Not be Good
1) **Small Scale Applications**: If your application is small to mid-sized and your database is not struggling with the 
data volume, sharding adds unnecessary complexity.
2) **Complex Transactions**: If your application requires a lot of complex transactions that involve multiple tables 
(especially tables that would be on different shards), sharding can make these operations much more complex and slower. 
3) **Frequent Inter-Shard Operations**: If your operations require frequent access to information located on different
shards, the costs of these operations could outweigh the benefits of sharding.
4) **Lack of Clear Sharding Key**: If there is not a clear attribute on which to shard the database, it might result in
uneven distribution of data, with some shards being heavy and others quite light, leading to inefficient resource 
5) utilization.

## Replication
**The entire database is copied onto multiple servers.** In the event that one server fails, the system can 
switch to a backup server. This is often used to handle read-heavy loads since it provides multiple copies of the data 
to handle requests. There are various replication strategies such as master-slave replication and multi-master 
replication each with their own pros and cons.

### Pros
1) **High Availability**: If one database server goes down, the system can immediately switch to a replicated server
with no data loss.
2) **Load Distribution**: You can distribute **read queries** across multiple replicas to reduce load on a single server 
and increase performance.
3) **Data Security**: Replicas act as backups, and in case of a catastrophic event like a disk failure, data loss can be
minimized or avoided entirely.
4) **Geographical Distribution**: If your application serves users globally, situating replicas in different 
geographical locations can reduce latency and improve access speed for distributed users.

### Cons
1) **Write Overhead**: Every **write operation** must be carried out on every replica, which can increase latency for 
write operations and put additional load on the system.
2) **Data Consistency**: There can be a delay in propagation of updates which may result in temporary inconsistencies 
between replicas (also known as eventual consistency).
3) **Storage Cost**: Each replica will consume the same amount of storage as the original database, so costs can 
increase considerably.
4) **Complexity**: Implementing replication, managing multiple replicas, handling failures, resolving conflicts, can add 
complexity to the system.

Scaling databases usually entails additional complexity and may require sophisticated orchestration to manage 
distribution of data, consistency, and fail over procedures. Some modern databases (like Amazon's DynamoDB or Google's 
Cloud Spanner) and NoSQL databases have built-in support for horizontal scaling making it easier to handle larger loads.

## Consistency Hashing
Consistent hashing is commonly used in distributed database systems to solve the problem of data distribution across 
multiple nodes. This is especially crucial when the database needs to be scaled up (by adding more nodes) or scaled down 
(by removing some nodes).

In traditional hashing, when we add or remove slots, all keys may potentially be remapped to different slots. However, 
in consistent hashing, when a slot is added or removed, only the keys that were assigned to the slot right before the 
added/removed one need to be remapped. The rest of the keys will still map to the same slot, maintaining consistency.

For example, in a key-value store type of NoSQL database, keys are often hashed to map to a specific node where the 
corresponding value is stored. If you are using a simple hash function and decide to add or remove nodes, this would 
require re-hashing and redistributing almost all keys across the new set of nodes, leading to a significant operations 
overhead and potential downtime.

**However, with consistent hashing:**
1) When you add a node, you only need to re-distribute a small portion of keys. The rest of the keys still map to the
same nodes.
2) When you remove a node, only the keys that were residing on that node need to be re-distributed.

This distribution mechanism is beneficial for big data applications as it reduces the need for data migration when 
scaling.

### Databases using Consistent Hashing
- **Cassandra**: Apache Cassandra uses consistent hashing for its data distribution. Cassandra has a ring-like 
architecture where each node in the system is assigned a random token which represents its position on the ring.
- **Riak**: Riak is another database that uses consistent hashing. Like Cassandra, each piece of data in Riak is 
associated with a unique key, which is then passed through a hashing function to determine which node (or nodes) on the 
ring the data is stored on.
- **DynamoDB**: Amazon's DynamoDB is built on principles that are laid out in Amazon’s Dynamo paper, which is a pioneer 
in employing consistent hashing in distributed databases.
- **Couchbase**: Couchbase uses consistent hashing to ensure that data distribution is evenly spread across all active 
nodes, and can be remapped efficiently as nodes join and leave the cluster.
- 
### Databases using Traditional Hashing
- **PostgreSQL, MySQL**: Traditional SQL databases generally don't use consistent hashing in the same way distributed 
databases do. They might use hash functions for specific tasks, such as hash indexes or hash joins, but these are 
implementation details and may vary.

## CAP Theorem
In the context of distributed data stores, the CAP Theorem is a concept that a distributed computing system is unable to 
simultaneously provide all three of the following guarantees:
- **Consistency (C)**: All nodes see the same data at the same time. In other words, a read operation will return the 
value of the most recent write operation, providing a linearizability guarantee.
- **Availability (A)**: Every non-failing node returns a response for all read and write requests in a reasonable amount 
of time. The system remains operational despite any node failures.
- **Partition Tolerance (P)**: The system continues to operate despite arbitrary message loss or failure of part of the 
system. This means the system can sustain network failures that separate groups of nodes (partitions) from each other.

The theorem's name, CAP, is derived from the initials of these three properties. According to the theorem, a distributed 
system can fulfill at most two of these three properties at the same time, but not all three.

### Examples
- A system designed for consistency and availability will be unable to function when a network partition occurs, as it 
can’t provide consistency across the partitions.
- A system that works for consistency and partition tolerance can’t guarantee immediate availability.
- A system emphasizing availability and partition tolerance might serve stale or incorrect data (not consistent).

The CAP theorem helps in understanding the trade-offs involved in designing and using distributed systems, and guides 
the developers and architects to make appropriate choices based on their system's requirements.