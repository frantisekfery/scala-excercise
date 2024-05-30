# Amazon Simple Queue Service (SQS)

Amazon Simple Queue Service (SQS) is a fully managed message queuing service provided by Amazon Web Services. It enables 
the decoupling of microservices, distributed systems, and serverless applications.

## Key aspects:
- **Message Queuing**: SQS stores messages from multiple producers to be processed by consumers. This helps to decouple 
the components of an application and scale them independently, improving reliability and efficiency.
- **Two Types of Queues**: SQS offers two types of message queues: standard queues (unlimited throughput, at-least-once 
delivery, best-effort ordering) and FIFO queues (up to 300 transactions per second, exactly-once processing, 
first-in-first-out delivery).
- **Scalability**: SQS services can be scaled up or down dynamically based on the workload.
- **Fault Tolerance**: SQS is designed to be highly available and durably stores messages across multiple geographically 
distant servers.
- **Security**: SQS supports Amazon's IAM service, enabling granular access control. It also supports encryption to keep
the content of messages private.
- 
### Pros
- **Fully Managed Service**: SQS is a fully managed service, meaning set up, operations, scaling, patching, and 
infrastructure management is handled by AWS.
- **Reliability**: SQS offers high availability and redundancy through its multiple AZ (Availability Zone) design.
- **Scaling**: The service scales automatically to work with the number of messages passing through it.
- **Integration**: SQS integrates well with other AWS services.

### Cons
- **Complex Pricing**: SQS charges can add up quickly in a high-volume environment.
- **No Delayed Delivery in FIFO Queues**: There is no support for delayed delivery of messages in FIFO queues.
- **Limited Features Compared to Competitors**: While SQS offers a solid set of features, competitors may provide 
additional functionality, like more granular control over message delivery and handling.

### Alternatives
- **Google Cloud Pub/Sub**: An equivalent service provided by Google Cloud that supports At-least-once message delivery 
and automatic and configurable message acknowledgment.
- **Azure Service Bus**: Microsoft Azure's service supporting publish-and-subscribe patterns, session handling, and 
transactional messaging.
- **RabbitMQ**: A popular open-source message-queue software that supports multiple messaging protocols and offers 
robust routing features.

### Use Cases
- **Decoupling Microservices**: SQS is great for asynchronous communications, specifically for connecting and decoupling
various microservices in a system.
- **Offloading Tasks from Front-end to back-end Systems**: SQS can be used to offload long-running or resource-intensive 
tasks from the front-end system to the back-end system.
- **Batch Operations**: Collecting data and processing it in batches is well-suited for an SQS queue.

### Where to avoid
- **Transactional Systems**: If your messages require atomicity, where operations need to happen all at once or not 
happen at all, SQS is not a perfect fit as it does not inherently support distributed transactions.
- **Real-time Systems**: SQS is not ideal in situations where latency is a critical factor. SQS does not guarantee 
real-time delivery speed for messages.

### Difference between SQS and Kinesis
- For Amazon SQS, once a message is consumed and acknowledged, it is deleted from the queue and can't be processed
again. If a message is not processed successfully, an application should not delete the message from the queue. SQS
allows the message to become visible again so that it can be consumed and processed by another component or at another 
time.
- For Amazon Kinesis Streams, data records are saved in the stream for a set period of time (from 24 hours up to 365 
days) after they are added, regardless of whether they are consumed or not. This means that multiple consumers can 
consume the same data records from the stream concurrently and at different times within the set retention period. When 
a data record is consumed doesn't affect its presence in the stream until the retention period elapses.