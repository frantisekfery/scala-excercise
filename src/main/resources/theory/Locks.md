# Locks

Locks are mechanisms that help ensure the integrity of data in concurrent programming, where multiple processes may need 
to access and modify shared data simultaneously. The purpose of locks is to prevent the same data from being modified by
more than one process or thread at a time, a situation that could lead to inconsistent or unexpected results.

## Deadlock:
A deadlock is a situation commonly encountered in multithreading or multiprocessing environments. In a deadlock 
condition, multiple processes (or threads) are unable to proceed because each is waiting for the other to release 
a resource.

### Example:
1) Process 1 holds Resource A and requests for Resource B.
2) Process 2 holds Resource B and requests for Resource A.

As a result, neither process can continue. Process 1 cannot complete its work because it's waiting for Resource B, which 
is held by Process 2. Similarly, Process 2 is waiting for Resource A, which is held by Process 1. Neither process will 
release the resources they hold until they have acquired the resources they need. This causes a circular chain of 
processes, with each process waiting for a resource that another process in the chain holds.

### Deadlock (all these conditions hold simultaneously)
- **Mutual Exclusion**: A resource can only be held by one process at a time.
- **Hold and Wait**: A process can hold on to resources while waiting for others.
- **No Preemption**: Only the process holding the resource can release it.
- **Circular Wait**: There exist a set {P1, P2, ..., PN} of waiting processes such that P1 is waiting for a resource held by 
P2, P2 is waiting for a resource held by P3 and so on until PN is waiting for a resource held by P1.

### How to prevent deadlock
1) **Lock Ordering**: Assign an order to the resources and ensure that all processes request resources in increasing 
order. This can eliminate circular waits and hence prevent deadlocks. For example, if the locks are resources A, B, and 
C, always acquire them in the order of A, B, and C and release them in the order of C, B, A. Following the same order in
all threads will negate the circular wait condition preventing deadlock.
2) **Two-Phase Locking (2PL)**: This protocol divides the execution of a transaction into two phases — growing and 
shrinking. In the growing phase, the transaction only acquires locks, and in the shrinking phase, it only releases 
locks. By ensuring that all lock acquisitions occur before any lock release, 2PL can reduce the likelihood of 
a deadlock.
3) **Timeouts**: When a process requests a resource, instead of allowing it to wait indefinitely, introduce a timeout. 
If the lock cannot be acquired within this limit, the process aborts and releases all its held locks, thus eliminating 
the deadlock situation.
4) **Dining Philosophers Problem Solution**: The dining philosophers problem is a classic example of multi-process 
synchronization which is used to simulate concurrency and deadlock.
- One of the solutions could be a philosopher picks up the left fork first and then the right one, he eats then leaves 
the forks down. But this solution can lead to deadlock.
- A well-designed locking solution to this problem is: let philosophers pick up the lower-numbered fork first. This way, 
the philosopher who wants to pick up forks 1 and 2 will pick up 1 first, and the philosopher who wants to pick up forks 
2 and 1 will also pick up 1 first, preventing a deadlock.