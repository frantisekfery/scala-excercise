# Transaction writes

Transactional writes are used in a variety of distributed systems contexts beyond just databases. If a system operation
involves multiple steps, and it's important that either all or none of these steps are carried out, then that operation
is a good candidate for a transactional approach even if it doesn't involve database operations.

Here are examples:
1) **Distributed File System**: Transactional writes can ensure that updates to a file are consistent across all nodes
   in the system, because you might have replicas. If a file is updated in two places at once, a transaction can ensure
   that these updates are done everywhere or are done nowhere (rollback)
2) **Message Queues**: In distributed messaging systems, transactional writes can ensure that a message is not lost in
   the event of a failure. The message is stored in a queue until it is successfully delivered and acknowledged by the
   recipient. A transaction might consist of receiving a message, processing it, and acknowledging it.
3) **Microservices Architecture**: In this design pattern, transactional writes can be part of a "saga" where multiple
   microservices are involved in handling a single high-level operation. For example, an e-commerce application might have
   separate services for user management, product inventory, and payment processing. If a user places an order,
   the application might need to check if the product is in stock, reserve it, charge the user's credit card, and update
   the user's order history. Each of these steps could be performed by different services and you'd want all steps
   to succeed or all to be rolled back if any fail. This is a similar concept to a database transaction but spread across
   multiple services, often managed using a pattern called the Saga pattern.
4) **Distributed Cache**: In a distributed cache (like Redis, Hazelcast), transaction ensures that all read-write
   operations happen atomically to avoid any inconsistencies. Atomicity is indeed referring to read-update-write sequences.
   Indivisible read-update-write sequences are important because two clients might continue independently based on an old
   value they've read, and end up overwriting each other's updates. Even though individual read and write operations might
   be idempotent, their combinations in the form of read-update-write sequences are not necessarily so.
5) **Process Coordination and Consensus Protocols**: Consensus protocols like Paxos and Raft, used in distributed
   systems for agreeing on a single data value (for example, which node is the leader), often leverage transaction-like
   concepts to ensure consistency. Transactions in these systems often involve multi-round voting processes to ensure that
   every node in the cluster agrees with the change, and that the system as a whole continues operating with a consistent
   view on its state.