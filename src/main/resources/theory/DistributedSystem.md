# Distributed Systems

## Microservices Architecture
This architecture style structures an application as a collection of loosely coupled services. Each service represents a 
specific business capability and can be developed, deployed, and scaled independently.

## Message-driven Architecture
This architecture is a design for applications where the trigger for computation is primarily the arrival of a message, 
such as user actions, sensor outputs, or messages from other programs. Messages are generally small, and the 
applications are designed to react to them promptly.

## API-driven Architecture
This architecture involves designing and building your application based around APIs, which act as building blocks. Your 
APIs encapsulate the business logic and the underlying database transactions, and they expose this functionality to 
other apps that can utilize the APIs. Web applications, mobile applications, and other services can all use these APIs 
to access the functionality they provide.

## Event-driven architecture
In this architecture, the production, detection, and reaction to events are the backbone of all the services. An 'event'
is a change in state, or an update, like a mouse click, an item being placed in a shopping cart on a website, or
services communicating that a task has been completed.

### Broker Responsibility
broker is a system component responsible for transferring messages (or events) from publishers (or producers) to any 
subscribers (or consumers) interested in those events.
The checkpoint is a concept that is commonly used in systems that handle message streams or any continuously updating 
log of events. The major role of a checkpoint is to track the progress of data consumption and provide fault-tolerance.

#### Here's a detailed explanation
1) **Checkpointing**: A checkpoint is a mechanism by which the system takes a "snapshot" of the progress of an
application or service at a point in time, and records it in a durable storage system (like a database or disk).

For example, in a stream processing application, a checkpoint might consist of the offsets (positions) up to which each
partition of the stream has been read. This state can be stored externally and represents a particular point in the 
stream processing pipeline.

2) **Recovery**: If a system crashes or a service goes down, upon restart, it needs to know from which point to continue 
processing the events. Trying to process from the beginning would be inefficient and might result in duplicate 
processing of events.

This is where the checkpoint comes to rescue. The service can retrieve the checkpoint data, see where it left off, and 
continue processing from that point, instead of starting from scratch. This provides fault-tolerance to the system.

3) **Broker's Responsibility**: In some systems, the broker itself may handle the checkpointing process. It keeps track 
of the events that have been delivered to each consumer, and which ones are acknowledged as processed.

However, in many modern systems like Apache Kafka or RabbitMQ, the broker delegates this task to the consumer. Each 
consumer reports its progress back to the broker by periodically updating its offset or position in the stream. This 
allows for more flexibility and better distributed load, as each consumer can progress at its own pace.