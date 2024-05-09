# Akka Cluster

Akka Cluster provides a fault-tolerant, decentralized, peer-to-peer based cluster membership service with no single 
point of failure or single point of bottleneck. It does this by using gossip protocols and an automatic failure 
detector.

Akka Cluster allows you to have an application running on multiple machines, with each machine being a node in the 
cluster. Nodes can join and leave the cluster, and Akka handles this in a graceful and consistent manner.

Akka Cluster ensures that there is no single point of failure or bottleneck when receiving and processing messages.

In Akka Cluster, you have a collection of actor systems (a system that can create and manage actors) spread across 
multiple machines, creating a network of nodes. They communicate by passing messages around, which is the cornerstone
of a distributed system.

The main challenge of building a distributed system lies in dealing with failures, network partitions and 
inconsistencies, and that's what Akka Cluster is perfect at. It provides several features to help build resilient, 
elastic, and responsive systems:
- **Cluster Membership**: Helps keep track and manage member nodes.
- **Failure Detection**: Detects failing nodes in the cluster.
- **Cluster Singleton**: Ensures that an actor runs on only one node at a time.
- **Cluster Sharding**: Distributes actors across several nodes in the cluster.
- **Distributed Data**: Shares mutable state among the nodes.

Remember that Akka Cluster should be used in scenarios where you need a high level of resiliency and you can tolerate 
relatively high latency.

## Cluster Membership
Akka cluster keeps track of all the nodes in the cluster. A node can either be a member of the cluster in various states
(Joining, Up, Leaving, etc.) or a non-member. Each node knows about all other nodes in the cluster, their status and 
roles, so it's decentralized with no single point of failure.

### Node states
- **Joining**: A node is in the Joining state when it wants to join the cluster. This is the initial state of a new 
node. The joining status is communicated to all nodes in the cluster.
- **Up**: Once the node has joined and is functioning correctly, it transitions to the Up state. It's the normal 
operating state of a node.
- **Leaving**: A node can be a part of the cluster but is intending to leave. A node announces its intention to leave 
the cluster and goes into the Leaving state.
- **Exiting**: This is the state that follows Leaving. It signifies that the node has been removed from the cluster and
no longer participates in its operations.
- **Down**: This state indicates a node that was suspected to be crashed or removed on purpose. It is separated from 
the cluster.
- **Removed**: The final state of a node, meaning it has been downed and removed from the cluster.
- **WeaklyUp**: A node in this state has been marked as part of the cluster but has not reached the Up state. This is 
usually used during network partitions, where the availability of a node might be in question, but it's still partially 
reachable.

## Gossip Protocol
Akka Cluster uses a gossip protocol for nodes to share knowledge about each other and the state of the cluster. The 
gossip info includes the cluster membership ring, the leader, the singleton actors, and more.

## Failure Detection
This mechanism identifies unreachable nodes by using an accrual failure detector. The suspicion level of failure is 
adjusted according to the communication behavior (network traffic) between nodes.

## Distributed Data
Akka Distributed Data (DD) allows you to share mutable data types by using Conflict-Free Replicated Data Types (CRDTs)
which are designed to allow eventual consistency semantics in an efficient manner.

## Cluster Singleton
For those times when you need to ensure that only one instance of a particular actor is running at any point in the
cluster, Akka provides Singleton actors. The actor is always running on the oldest node in the cluster.

## Cluster Sharding
This is an automatic way of distributing actors across many nodes of an Akka Cluster. Sharding helps when you need to 
track many entities, each with its own persistent state and behavior.

## Cluster Routers
Akka provides several different types of routers to help in actors’ location-transparent routing of messages. 
Cluster-enabled routers automatically route messages to actors running on any node in the cluster.

## Cluster Pub-Sub
The Publish-Subscribe model is a messaging pattern where senders (publishers) categorize messages into topics, and send 
them to a broker. Receivers (subscribers) then subscribe to those topics on the same broker.
